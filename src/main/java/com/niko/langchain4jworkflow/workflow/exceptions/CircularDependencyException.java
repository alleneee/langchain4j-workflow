package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 循环依赖异常
 * 当工作流中的节点存在循环依赖关系时抛出此异常
 */
public class CircularDependencyException extends WorkflowException {

    /**
     * 构造函数
     * @param message 错误信息
     */
    public CircularDependencyException(String message) {
        super(message);
    }
}

