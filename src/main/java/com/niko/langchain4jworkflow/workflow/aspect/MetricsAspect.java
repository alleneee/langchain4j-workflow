package com.niko.langchain4jworkflow.workflow.aspect;

import com.niko.langchain4jworkflow.workflow.metrics.WorkflowMetricsCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 工作流指标收集切面
 * 用于收集工作流执行的相关指标
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MetricsAspect {
    private final WorkflowMetricsCollector metricsCollector;

    /**
     * 在工作流执行前后收集指标
     * @param joinPoint 切点
     * @return 方法执行结果
     * @throws Throwable 执行异常
     */
    @Around("@annotation(com.niko.langchain4jworkflow.workflow.annotation.CollectMetrics)")
    public Object collectMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();

        try {
            // 记录调用次数
            metricsCollector.incrementCounter(
                    methodName + ".calls",
                    "class", joinPoint.getTarget().getClass().getSimpleName()
            );

            // 执行方法
            Object result = joinPoint.proceed();

            // 记录成功次数
            metricsCollector.incrementCounter(
                    methodName + ".success",
                    "class", joinPoint.getTarget().getClass().getSimpleName()
            );

            return result;

        } catch (Exception e) {
            // 记录失败次数
            metricsCollector.incrementCounter(
                    methodName + ".failures",
                    "class", joinPoint.getTarget().getClass().getSimpleName(),
                    "error", e.getClass().getSimpleName()
            );
            throw e;

        } finally {
            // 记录执行时间
            long duration = System.currentTimeMillis() - startTime;
            metricsCollector.recordTime(
                    methodName + ".duration",
                    duration,
                    "class", joinPoint.getTarget().getClass().getSimpleName()
            );

            // 记录自定义指标
            if (joinPoint.getArgs().length > 0) {
                Arrays.stream(joinPoint.getArgs()).forEach(arg ->
                        metricsCollector.recordValue(
                                methodName + "." + arg.getClass().getSimpleName(),
                                extractMetricValue(arg, joinPoint),
                                "class", joinPoint.getTarget().getClass().getSimpleName()
                        )
                );
            }
        }
    }

    private double extractMetricValue(Object arg, ProceedingJoinPoint joinPoint) {
        // 实现从方法参数或返回值中提取指标值的逻辑
        return 0.0;
    }
}