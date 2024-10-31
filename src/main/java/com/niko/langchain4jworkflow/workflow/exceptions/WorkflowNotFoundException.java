package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 工作流未找到异常
 */
public class WorkflowNotFoundException extends WorkflowException {

    private final String workflowName;

    public WorkflowNotFoundException(String workflowName) {
        super("Workflow not found: " + workflowName);
        this.workflowName = workflowName;
    }

    public String getWorkflowName() {
        return workflowName;
    }
}
