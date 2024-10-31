package com.niko.langchain4jworkflow.workflow.event;

import lombok.Getter;
import java.time.Duration;

@Getter
public class NodeRetryEvent extends WorkflowEvent {
    private final String nodeName;
    private final int attemptNumber;
    private final Throwable error;
    private final Duration delay;

    public NodeRetryEvent(Object source, String workflowName, String executionId, 
                         String nodeName, int attemptNumber, Duration delay, Throwable error) {
        super(source, workflowName, executionId);
        this.nodeName = nodeName;
        this.attemptNumber = attemptNumber;
        this.delay = delay;
        this.error = error;
    }

    public long getDelayMillis() {
        return delay != null ? delay.toMillis() : 0L;
    }
}