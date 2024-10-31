package com.niko.langchain4jworkflow.workflow.event;

import lombok.Getter;

@Getter
public class NodeStartEvent extends WorkflowEvent {
    private final String nodeName;

    public NodeStartEvent(
            String workflowName,
            String executionId,
            String nodeName) {
        super(workflowName, executionId);
        this.nodeName = nodeName;
    }
}