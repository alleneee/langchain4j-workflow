package com.niko.langchain4jworkflow.workflow.core;


import com.niko.langchain4jworkflow.workflow.annotation.Async;
import com.niko.langchain4jworkflow.workflow.annotation.Retry;
import com.niko.langchain4jworkflow.workflow.annotation.StateVariable;
import com.niko.langchain4jworkflow.workflow.annotation.Workflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okio.Timeout;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class WorkflowScanner {
    private final WorkflowRegistry registry;

    public Optional<WorkflowDefinition> scanWorkflow(Class<?> clazz) {
        Workflow workflowAnn = AnnotationUtils.findAnnotation(
                clazz, Workflow.class);

        if (workflowAnn == null) {
            return Optional.empty();
        }

        try {
            // 创建图构建器
            GraphBuilder builder = new GraphBuilder();
            // 扫描所有节点
            scanNodes(clazz, builder);

            // 构建工作流定义
            WorkflowDefinition workflow = builder.build(getWorkflowName(workflowAnn, clazz));

            // 设置工作流配置
            workflow.setConfig(scanWorkflowConfig(clazz));

            return Optional.of(workflow);

        } catch (Exception e) {
            log.error("Failed to scan workflow: {}",
                    clazz.getName(), e);
            return Optional.empty();
        }
    }

    private String getWorkflowName(Workflow workflowAnn, Class<?> clazz) {
        String name = workflowAnn.name();
        if (name.isEmpty()) {
            name = workflowAnn.value();
        }
        if (name.isEmpty()) {
            name = clazz.getSimpleName();
        }
        return name;
    }

    private void scanNodes(Class<?> clazz, GraphBuilder builder) {
        ReflectionUtils.doWithMethods(clazz, method -> {
            Node nodeAnn = AnnotationUtils.findAnnotation(
                    method, Node.class);
            if (nodeAnn != null) {
                Node node = buildNode(method, nodeAnn);
                builder.addNode(node);
            }
        });
    }

    private Node buildNode(Method method, Node nodeAnn) {
        return Node.builder()
                .name(getNodeName(nodeAnn, method))
                .type(nodeAnn.type())
                .method(method)
                .dependencies(Arrays.asList(nodeAnn.dependsOn()))
                .config(buildNodeConfig(method))
                .inputs(scanInputs(method))
                .outputs(scanOutputs(method))
                .build();
    }

    private String getNodeName(Node nodeAnn, Method method) {
        String name = nodeAnn.name();
        return name.isEmpty() ? method.getName() : name;
    }

    private Node.NodeConfig buildNodeConfig(Method method) {
        return Node.NodeConfig.builder()
                .timeout(getTimeout(method))
                .retryConfig(getRetryConfig(method))
                .async(isAsync(method))
                .systemPrompt(getSystemPrompt(method))
                .build();
    }

    private Duration getTimeout(Method method) {
        Timeout timeoutAnn = AnnotationUtils.findAnnotation(
                method, Timeout.class);
        return timeoutAnn != null ?
                Duration.of(timeoutAnn.value(), timeoutAnn.unit()) :
                null;
    }

    private Node.RetryConfig getRetryConfig(Method method) {
        Retry retryAnn = AnnotationUtils.findAnnotation(
                method, Retry.class);
        if (retryAnn == null) {
            return null;
        }

        return Node.RetryConfig.builder()
                .maxAttempts(retryAnn.maxAttempts())
                .initialDelay(Duration.ofMillis(retryAnn.initialDelay()))
                .multiplier(retryAnn.multiplier())
                .retryableExceptions(Arrays.asList(retryAnn.retryFor()))
                .build();
    }

    private boolean isAsync(Method method) {
        return AnnotationUtils.findAnnotation(method, Async.class) != null;
    }

    private String getSystemPrompt(Method method) {
        Node nodeAnn = AnnotationUtils.findAnnotation(method, Node.class);
        return nodeAnn != null ? nodeAnn.systemPrompt() : null;
    }

    private Map<String, Class<?>> scanInputs(Method method) {
        Map<String, Class<?>> inputs = new HashMap<>();
        Arrays.stream(method.getParameters())
                .forEach(param -> {
                    StateVariable stateVar =
                            AnnotationUtils.findAnnotation(
                                    param, StateVariable.class);
                    if (stateVar != null) {
                        String name = stateVar.value().isEmpty() ?
                                param.getName() : stateVar.value();
                        inputs.put(name, param.getType());
                    }
                });
        return inputs;
    }

    private Map<String, Class<?>> scanOutputs(Method method) {
        Map<String, Class<?>> outputs = new HashMap<>();
        Class<?> returnType = method.getReturnType();
        if (returnType != void.class) {
            outputs.put("result", returnType);
        }
        return outputs;
    }

    private WorkflowDefinition.WorkflowConfig scanWorkflowConfig(Class<?> clazz) {
        return WorkflowDefinition.WorkflowConfig.builder()
                .timeout(getWorkflowTimeout(clazz))
                .retryConfig(getWorkflowRetryConfig(clazz))
                .cacheConfig(getWorkflowCacheConfig(clazz))
                .monitorConfig(getWorkflowMonitorConfig(clazz))
                .asyncEnabled(isWorkflowAsync(clazz))
                .build();
    }

    private Duration getWorkflowTimeout(Class<?> clazz) {
        Timeout timeoutAnn = AnnotationUtils.findAnnotation(clazz, Timeout.class);
        return timeoutAnn != null ?
                Duration.of(timeoutAnn.value(), timeoutAnn.unit()) :
                null;
    }

    private WorkflowDefinition.WorkflowConfig.RetryConfig getWorkflowRetryConfig(Class<?> clazz) {
        Retry retryAnn = AnnotationUtils.findAnnotation(clazz, Retry.class);
        if (retryAnn == null) {
            return null;
        }

        return WorkflowDefinition.WorkflowConfig.RetryConfig.builder()
                .maxAttempts(retryAnn.maxAttempts())
                .delay(Duration.ofMillis(retryAnn.initialDelay()))
                .multiplier(retryAnn.multiplier())
                .retryableExceptions(Arrays.asList(retryAnn.retryFor()))
                .build();
    }

    private WorkflowDefinition.WorkflowConfig.CacheConfig getWorkflowCacheConfig(Class<?> clazz) {
        // TODO: 实现缓存配置扫描
        return null;
    }

    private WorkflowDefinition.WorkflowConfig.MonitorConfig getWorkflowMonitorConfig(Class<?> clazz) {
        // TODO: 实现监控配置扫描
        return null;
    }

    private boolean isWorkflowAsync(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, Async.class) != null;
    }
}
