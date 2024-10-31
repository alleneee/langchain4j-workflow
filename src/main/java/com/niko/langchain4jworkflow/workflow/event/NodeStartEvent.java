package com.niko.langchain4jworkflow.workflow.event;

import lombok.Getter;

/**
 * 节点开始事件
 * 在工作流中的节点开始执行时触发
 */
@Getter
public class NodeStartEvent extends WorkflowEvent {
    /**
     * 节点名称
     */
    private final String nodeName;

    /**
     * 构造函数
     * @param source 事件源对象
     * @param workflowName 工作流名称
     * @param executionId 执行ID
     * @param nodeName 节点名称
     */
    public NodeStartEvent(Object source, String workflowName, String executionId, String nodeName) {
        super(source, workflowName, executionId);
        this.nodeName = nodeName;
    }
}