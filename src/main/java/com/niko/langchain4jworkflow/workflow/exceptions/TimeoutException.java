package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 超时异常
 */
public class TimeoutException extends WorkflowException {

    private final long timeoutMillis;

    public TimeoutException(String operation, long timeoutMillis) {
        super(String.format("Operation '%s' timed out after %d ms",
                operation, timeoutMillis));
        this.timeoutMillis = timeoutMillis;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }
}
