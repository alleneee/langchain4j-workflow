package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 超时异常
 * 当工作流或节点执行超时时抛出此异常
 */
public class TimeoutException extends WorkflowException {

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
