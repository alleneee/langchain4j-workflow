package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 节点执行异常
 */
public class NodeExecutionException extends WorkflowException {

    private final String nodeName;

    public NodeExecutionException(String nodeName, String message) {
        super(String.format("Node '%s' execution failed: %s", nodeName, message));
        this.nodeName = nodeName;
    }

    public NodeExecutionException(
            String nodeName,
            String message,
            Throwable cause) {
        super(String.format("Node '%s' execution failed: %s",
                nodeName, message), cause);
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }
}
