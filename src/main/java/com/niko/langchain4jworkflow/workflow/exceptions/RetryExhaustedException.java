package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 重试耗尽异常
 */
public class RetryExhaustedException extends WorkflowException {

    private final int attempts;
    private final String operation;

    public RetryExhaustedException(
            String operation,
            int attempts,
            Throwable cause) {
        super(String.format(
                "Retry exhausted for operation '%s' after %d attempts",
                operation, attempts), cause);
        this.operation = operation;
        this.attempts = attempts;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getOperation() {
        return operation;
    }
}
