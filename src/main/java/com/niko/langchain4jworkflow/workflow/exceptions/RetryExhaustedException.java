package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 重试耗尽异常
 * 当节点重试次数达到上限时抛出此异常
 */
public class RetryExhaustedException extends WorkflowException {

    /**
     * 构造函数
     * @param nodeName 节点名称
     * @param attempts 重试次数
     */
    public RetryExhaustedException(String nodeName, int attempts) {
        super(String.format("Node '%s' retry exhausted after %d attempts", nodeName, attempts));
    }
}
