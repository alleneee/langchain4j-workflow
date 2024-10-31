package com.niko.langchain4jworkflow.workflow.event;

public class WorkflowCancelEvent extends WorkflowEvent {
    public WorkflowCancelEvent(Object source, String workflowName, String executionId) {
        super(source, workflowName, executionId);
    }
}