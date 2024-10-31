package com.niko.langchain4jworkflow.workflow.event;

import lombok.Getter;

@Getter
public class NodeRetryEvent extends WorkflowEvent {
    private final String nodeName;
    private final int attemptNumber;
    private final Throwable error;
    private final long delayMillis;

    public NodeRetryEvent(
            String workflowName,
            String executionId,
            String nodeName,
            int attemptNumber,
            Throwable error,
            long delayMillis) {
        super(workflowName, executionId);
        this.nodeName = nodeName;
        this.attemptNumber = attemptNumber;
        this.error = error;
        this.delayMillis = delayMillis;
    }
}