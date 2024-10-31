package com.niko.langchain4jworkflow.workflow.aspect;

import com.niko.langchain4jworkflow.core.WorkflowContext;
import com.niko.langchain4jworkflow.core.WorkflowState;
import com.niko.langchain4jworkflow.workflow.annotation.Workflow;
import com.niko.langchain4jworkflow.workflow.core.WorkflowEngine;
import com.niko.langchain4jworkflow.workflow.metrics.MetricsRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Slf4j
public class WorkflowAspect {

    private final WorkflowEngine workflowEngine;
    private final MetricsRegistry metricsRegistry;
    private final Map<String, WorkflowMetrics> metricsMap = new ConcurrentHashMap<>();

    public WorkflowAspect(WorkflowEngine workflowEngine, MetricsRegistry metricsRegistry) {
        this.workflowEngine = workflowEngine;
        this.metricsRegistry = metricsRegistry;
    }

    @Around("@annotation(com.niko.langchain4jworkflow.workflow.annotation.Workflow)")
    public Object aroundWorkflow(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Workflow workflow = method.getAnnotation(Workflow.class);
        String workflowName = workflow.name().isEmpty() ? method.getName() : workflow.name();

        // 创建或获取工作流上下文
        WorkflowContext context = createOrGetContext(joinPoint, workflowName);
        
        // 记录开始时间
        Instant start = Instant.now();
        
        try {
            // 前置处理
            beforeWorkflow(context);
            
            // 执行工作流
            Object result = joinPoint.proceed();
            
            // 后置处理
            afterWorkflow(context);
            
            // 更新指标
            updateMetrics(workflowName, start, null);
            
            return result;
        } catch (Exception e) {
            // 异常处理
            handleWorkflowException(context, e);
            
            // 更新指标
            updateMetrics(workflowName, start, e);
            
            throw e;
        }
    }

    private WorkflowContext createOrGetContext(ProceedingJoinPoint joinPoint, String workflowName) {
        // 将方法参数转换为工作流变量
        Object[] args = joinPoint.getArgs();
        Map<String, Object> variables = new HashMap<>();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        
        for (int i = 0; i < args.length; i++) {
            variables.put(parameterNames[i], args[i]);
        }

        // 创建工作流状态
        WorkflowState state = new WorkflowState();
        state.getVariables().putAll(variables);

        // 创建工作流上下文
        return WorkflowContext.builder()
                .workflowId(state.getWorkflowId())
                .workflowName(workflowName)
                .state(state)
                .build();
    }

    private void beforeWorkflow(WorkflowContext context) {
        log.info("Starting workflow: {} [ID: {}]", 
                context.getWorkflowName(), 
                context.getWorkflowId());
        log.debug("Workflow input variables: {}", context.getState().getVariables());
    }

    private void afterWorkflow(WorkflowContext context) {
        log.info("Completed workflow: {} [ID: {}]", 
                context.getWorkflowName(), 
                context.getWorkflowId());
        log.debug("Workflow output variables: {}", context.getState().getVariables());
    }

    private void handleWorkflowException(WorkflowContext context, Exception e) {
        log.error("Error in workflow: {} [ID: {}]", 
                context.getWorkflowName(), 
                context.getWorkflowId(), 
                e);
        context.getState().markAsFailed(e.getMessage());
    }

    private void updateMetrics(String workflowName, Instant start, Exception error) {
        Duration duration = Duration.between(start, Instant.now());
        WorkflowMetrics metrics = metricsMap.computeIfAbsent(workflowName, 
                k -> new WorkflowMetrics());
        
        metrics.addExecution(duration, error);
        
        // 更新指标注册表
        if (metricsRegistry != null) {
            metricsRegistry.recordWorkflowExecution(workflowName, duration, error == null);
        }
    }

    @lombok.Data
    private static class WorkflowMetrics {
        private long totalExecutions = 0;
        private long failedExecutions = 0;
        private Duration totalDuration = Duration.ZERO;
        private Duration maxDuration = Duration.ZERO;
        private Duration minDuration = Duration.ofDays(365);

        public synchronized void addExecution(Duration duration, Exception error) {
            totalExecutions++;
            if (error != null) {
                failedExecutions++;
            }
            
            totalDuration = totalDuration.plus(duration);
            maxDuration = duration.compareTo(maxDuration) > 0 ? duration : maxDuration;
            minDuration = duration.compareTo(minDuration) < 0 ? duration : minDuration;
        }

        public Duration getAverageDuration() {
            return totalExecutions > 0 
                    ? totalDuration.dividedBy(totalExecutions) 
                    : Duration.ZERO;
        }
    }
}
