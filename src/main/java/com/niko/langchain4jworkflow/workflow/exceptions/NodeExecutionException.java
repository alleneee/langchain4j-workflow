package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 节点执行异常
 * 当工作流节点执行失败时抛出此异常
 */
public class NodeExecutionException extends WorkflowException {

    /**
     * 节点名称
     */
    private final String nodeName;

    /**
     * 构造函数
     * @param nodeName 节点名称
     * @param message 错误信息
     */
    public NodeExecutionException(String nodeName, String message) {
        super(String.format("Node '%s' execution failed: %s", nodeName, message));
        this.nodeName = nodeName;
    }

    /**
     * 构造函数
     * @param nodeName 节点名称
     * @param message 错误信息
     * @param cause 原始异常
     */
    public NodeExecutionException(String nodeName, String message, Throwable cause) {
        super(String.format("Node '%s' execution failed: %s", nodeName, message), cause);
        this.nodeName = nodeName;
    }

    /**
     * 获取节点名称
     * @return 节点名称
     */
    public String getNodeName() {
        return nodeName;
    }
}
