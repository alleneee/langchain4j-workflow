package com.niko.langchain4jworkflow.workflow.event;

import com.niko.langchain4jworkflow.workflow.metrics.MetricsRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowEventListener {

    private final MetricsRegistry metricsRegistry;

    @EventListener
    public void onWorkflowStart(WorkflowStartEvent event) {
        log.info("Workflow started: {} ({})",
                event.getWorkflowName(),
                event.getExecutionId());

        metricsRegistry.incrementCounter(
                "workflow.starts",
                "workflow", event.getWorkflowName()
        );
    }

    @EventListener
    public void onWorkflowComplete(WorkflowCompleteEvent event) {
        log.info("Workflow completed: {} ({})",
                event.getWorkflowName(),
                event.getExecutionId());

        metricsRegistry.incrementCounter(
                "workflow.completions",
                "workflow", event.getWorkflowName()
        );
    }

    @EventListener
    public void onWorkflowError(WorkflowErrorEvent event) {
        log.error("Workflow failed: {} ({})",
                event.getWorkflowName(),
                event.getExecutionId(),
                event.getError());

        metricsRegistry.incrementCounter(
                "workflow.failures",
                "workflow", event.getWorkflowName(),
                "error", event.getError().getClass().getSimpleName()
        );
    }

    @EventListener
    public void onWorkflowCancel(WorkflowCancelEvent event) {
        log.info("Workflow cancelled: {} ({})",
                event.getWorkflowName(),
                event.getExecutionId());

        metricsRegistry.incrementCounter(
                "workflow.cancellations",
                "workflow", event.getWorkflowName()
        );
    }

    @EventListener
    public void onNodeStart(NodeStartEvent event) {
        log.debug("Node started: {}.{} ({})",
                event.getWorkflowName(),
                event.getNodeName(),
                event.getExecutionId());

        metricsRegistry.incrementCounter(
                "node.starts",
                "workflow", event.getWorkflowName(),
                "node", event.getNodeName()
        );
    }

    @EventListener
    public void onNodeComplete(NodeCompleteEvent event) {
        log.debug("Node completed: {}.{} ({})",
                event.getWorkflowName(),
                event.getNodeName(),
                event.getExecutionId());

        metricsRegistry.incrementCounter(
                "node.completions",
                "workflow", event.getWorkflowName(),
                "node", event.getNodeName()
        );
    }

    @EventListener
    public void onNodeError(NodeErrorEvent event) {
        log.error("Node failed: {}.{} ({})",
                event.getWorkflowName(),
                event.getNodeName(),
                event.getExecutionId(),
                event.getError());

        metricsRegistry.incrementCounter(
                "node.failures",
                "workflow", event.getWorkflowName(),
                "node", event.getNodeName(),
                "error", event.getError().getClass().getSimpleName()
        );
    }

    @EventListener
    public void onNodeRetry(NodeRetryEvent event) {
        log.warn("Node retry: {}.{} ({}) - Attempt {}, Delay {}ms",
                event.getWorkflowName(),
                event.getNodeName(),
                event.getExecutionId(),
                event.getAttemptNumber(),
                event.getDelayMillis()
        );

        metricsRegistry.incrementCounter(
                "node.retries",
                "workflow", event.getWorkflowName(),
                "node", event.getNodeName()
        );
    }
}
