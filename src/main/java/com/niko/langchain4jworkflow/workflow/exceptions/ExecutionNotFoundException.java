package com.niko.langchain4jworkflow.workflow.exceptions;

public class ExecutionNotFoundException extends WorkflowException {
    public ExecutionNotFoundException(String executionId) {
        super("Workflow execution not found: " + executionId);
    }
}
