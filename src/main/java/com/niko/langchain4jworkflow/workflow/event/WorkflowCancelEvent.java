package com.niko.langchain4jworkflow.workflow.event;

import lombok.Getter;

@Getter
public class WorkflowCancelEvent extends WorkflowEvent {
    public WorkflowCancelEvent(String workflowName, String executionId) {
        super(workflowName, executionId);
    }
}