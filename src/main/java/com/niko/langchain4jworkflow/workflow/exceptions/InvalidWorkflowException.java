package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 无效工作流异常
 * 当工作流定义不符合要求时抛出此异常
 */
public class InvalidWorkflowException extends WorkflowException {

    /**
     * 构造函数
     * @param message 错误信息
     */
    public InvalidWorkflowException(String message) {
        super(message);
    }

    /**
     * 构造函数
     * @param message 错误信息
     * @param cause 原始异常
     */
    public InvalidWorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}