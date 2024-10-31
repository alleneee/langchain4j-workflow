package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 执行实例未找到异常
 * 当查询的工作流执行实例不存在时抛出此异常
 */
public class ExecutionNotFoundException extends WorkflowException {
    
    /**
     * 构造函数
     * @param executionId 执行ID
     */
    public ExecutionNotFoundException(String executionId) {
        super("Execution not found: " + executionId);
    }
}
