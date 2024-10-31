package com.niko.langchain4jworkflow.workflow.metrics;

import com.niko.langchain4jworkflow.workflow.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工作流指标收集器
 * 负责收集和管理工作流执行的各项指标
 */
@Component
@RequiredArgsConstructor
public class WorkflowMetricsCollector {
    private final MetricsRegistry metricsRegistry;
    private final Map<String, Instant> startTimes = new ConcurrentHashMap<>();

    /**
     * 记录工作流开始执行
     * @param workflowId 工作流ID
     */
    @EventListener
    public void onWorkflowStart(WorkflowStartEvent event) {
        startTimes.put(event.getExecutionId(), Instant.now());
        metricsRegistry.incrementCounter(
                "workflow.starts",
                "workflow", event.getWorkflowName());
    }

    /**
     * 记录工作流执行完成
     * @param workflowId 工作流ID
     * @param duration 执行持续时间
     */
    @EventListener
    public void onWorkflowComplete(WorkflowCompleteEvent event) {
        recordCompletion(event.getExecutionId(), event.getWorkflowName(), true);
    }

    @EventListener
    public void onWorkflowError(WorkflowErrorEvent event) {
        recordCompletion(event.getExecutionId(), event.getWorkflowName(), false);
        metricsRegistry.incrementCounter(
                "workflow.errors",
                "workflow", event.getWorkflowName(),
                "error", event.getError().getClass().getSimpleName());
    }

    @EventListener
    public void onWorkflowCancel(WorkflowCancelEvent event) {
        recordCompletion(event.getExecutionId(), event.getWorkflowName(), false);
        metricsRegistry.incrementCounter(
                "workflow.cancellations",
                "workflow", event.getWorkflowName());
    }

    private void recordCompletion(String executionId, String workflowName, boolean success) {
        Instant startTime = startTimes.remove(executionId);
        if (startTime != null) {
            Duration duration = Duration.between(startTime, Instant.now());
            metricsRegistry.recordWorkflowExecution(workflowName, duration, success);
        }
    }

    public void incrementCounter(String name, String... tags) {
        metricsRegistry.incrementCounter(name, tags);
    }

    public void recordTime(String name, long duration, String... tags) {
        metricsRegistry.recordTime(name, duration, tags);
    }

    public void recordValue(String name, double value, String... tags) {
        metricsRegistry.recordValue(name, value, tags);
    }
}
