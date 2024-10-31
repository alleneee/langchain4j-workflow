package com.niko.langchain4jworkflow.workflow.metrics;


import com.niko.langchain4jworkflow.workflow.core.WorkflowState;
import com.niko.langchain4jworkflow.workflow.event.NodeErrorEvent;
import com.niko.langchain4jworkflow.workflow.event.WorkflowStartEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WorkflowMetricsCollector {
    private final MetricsRegistry metricsRegistry;
    private final Map<String, WorkflowMetrics> activeWorkflows =
            new ConcurrentHashMap<>();

    @EventListener
    public void onWorkflowStart(WorkflowStartEvent event) {
        String workflowId = event.getWorkflowId();
        WorkflowMetrics.Builder metricsBuilder = WorkflowMetrics.builder()
                .workflowId(workflowId)
                .workflowName(event.getWorkflowName())
                .nodeMetrics(new HashMap<>());

        activeWorkflows.put(workflowId, metricsBuilder);

        metricsRegistry.incrementCounter(
                "workflow.starts",
                "workflow", event.getWorkflowName()
        );
    }

    @EventListener
    public void onWorkflowComplete(WorkflowCompleteEvent event) {
        String workflowId = event.getWorkflowId();
        WorkflowMetrics.Builder metricsBuilder =
                activeWorkflows.remove(workflowId);

        if (metricsBuilder != null) {
            WorkflowState state = event.getState();
            WorkflowMetrics metrics = buildMetrics(metricsBuilder, state);

            // 记录完成指标
            recordCompletionMetrics(metrics);
        }
    }

    @EventListener
    public void onNodeComplete(NodeCompleteEvent event) {
        String workflowId = event.getWorkflowId();
        WorkflowMetrics.Builder metricsBuilder =
                activeWorkflows.get(workflowId);

        if (metricsBuilder != null) {
            String nodeName = event.getNodeName();
            Duration duration = Duration.ofMillis(event.getDuration());

            // 更新节点指标
            WorkflowMetrics.NodeMetrics nodeMetrics =
                    WorkflowMetrics.NodeMetrics.builder()
                            .nodeName(nodeName)
                            .duration(duration)
                            .attempts(event.getAttempts())
                            .successful(true)
                            .build();

            metricsBuilder.nodeMetrics.put(nodeName, nodeMetrics);

            // 记录节点指标
            recordNodeMetrics(nodeName, nodeMetrics);
        }
    }

    @EventListener
    public void onNodeError(NodeErrorEvent event) {
        String workflowId = event.getWorkflowId();
        WorkflowMetrics.Builder metricsBuilder =
                activeWorkflows.get(workflowId);

        if (metricsBuilder != null) {
            String nodeName = event.getNodeName();

            // 更新节点错误指标
            WorkflowMetrics.NodeMetrics nodeMetrics =
                    WorkflowMetrics.NodeMetrics.builder()
                            .nodeName(nodeName)
                            .attempts(event.getAttempts())
                            .successful(false)
                            .errorType(event.getError().getClass().getSimpleName())
                            .build();

            metricsBuilder.nodeMetrics.put(nodeName, nodeMetrics);

            // 记录错误指标
            recordNodeErrorMetrics(nodeName, nodeMetrics);
        }
    }

    private WorkflowMetrics buildMetrics(
            WorkflowMetrics.Builder builder,
            WorkflowState state) {
        return builder
                .duration(Duration.ofMillis(state.getDuration()))
                .nodeCount(state.getExecutionHistory().size())
                .successfulNodes((int) state.getExecutionHistory().values().stream()
                        .filter(info -> info.getError() == null)
                        .count())
                .failedNodes((int) state.getExecutionHistory().values().stream()
                        .filter(info -> info.getError() != null)
                        .count())
                .retryCount((int) state.getExecutionHistory().values().stream()
                        .mapToInt(info -> info.getAttempts() - 1)
                        .sum())
                .build();
    }

    private void recordCompletionMetrics(WorkflowMetrics metrics) {
        String workflowName = metrics.getWorkflowName();

        metricsRegistry.recordTime(
                "workflow.duration",
                metrics.getDuration().toMillis(),
                "workflow", workflowName
        );

        metricsRegistry.recordValue(
                "workflow.nodes.total",
                metrics.getNodeCount(),
                "workflow", workflowName
        );

        metricsRegistry.recordValue(
                "workflow.nodes.successful",
                metrics.getSuccessfulNodes(),
                "workflow", workflowName
        );

        metricsRegistry.recordValue(
                "workflow.nodes.failed",
                metrics.getFailedNodes(),
                "workflow", workflowName
        );

        metricsRegistry.recordValue(
                "workflow.retries",
                metrics.getRetryCount(),
                "workflow", workflowName
        );
    }

    private void recordNodeMetrics(
            String nodeName,
            WorkflowMetrics.NodeMetrics metrics) {
        metricsRegistry.recordTime(
                "node.duration",
                metrics.getDuration().toMillis(),
                "node", nodeName
        );

        metricsRegistry.recordValue(
                "node.attempts",
                metrics.getAttempts(),
                "node", nodeName
        );
    }

    private void recordNodeErrorMetrics(
            String nodeName,
            WorkflowMetrics.NodeMetrics metrics) {
        metricsRegistry.incrementCounter(
                "node.errors",
                "node", nodeName,
                "error", metrics.getErrorType()
        );

        metricsRegistry.recordValue(
                "node.attempts",
                metrics.getAttempts(),
                "node", nodeName
        );
    }
}
