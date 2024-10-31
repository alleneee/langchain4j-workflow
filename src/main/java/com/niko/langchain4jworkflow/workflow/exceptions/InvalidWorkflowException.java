package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 无效工作流异常
 */
public class InvalidWorkflowException extends WorkflowException {

    public InvalidWorkflowException(String message) {
        super(message);
    }

    public InvalidWorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}