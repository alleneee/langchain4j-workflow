package com.niko.langchain4jworkflow.workflow.exceptions;

import lombok.Getter;

/**
 * 工作流执行异常
 */
@Getter
public class WorkflowExecutionException extends WorkflowException {
    public WorkflowExecutionException(String message) {
        super(message);
    }

    public WorkflowExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
