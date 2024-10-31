package com.niko.langchain4jworkflow.workflow.event;

import lombok.Getter;

@Getter
public class NodeErrorEvent extends WorkflowEvent {
    private final String nodeName;
    private final Throwable error;

    public NodeErrorEvent(
            String workflowName,
            String executionId,
            String nodeName,
            Throwable error) {
        super(workflowName, executionId);
        this.nodeName = nodeName;
        this.error = error;
    }
}
