package com.niko.langchain4jworkflow.workflow.event;

import lombok.Getter;

@Getter
public class WorkflowErrorEvent extends WorkflowEvent {
    private final Throwable error;

    public WorkflowErrorEvent(
            String workflowName,
            String executionId,
            Throwable error) {
        super(workflowName, executionId);
        this.error = error;
    }
}
