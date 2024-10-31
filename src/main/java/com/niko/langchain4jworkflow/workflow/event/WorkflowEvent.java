package com.niko.langchain4jworkflow.workflow.event;

import lombok.Getter;

/**
 * 工作流事件基类
 * 所有工作流相关事件的父类，提供基本的事件属性和行为
 */
@Getter
public abstract class WorkflowEvent {
    /**
     * 事件源对象
     */
    private final Object source;

    /**
     * 工作流名称
     */
    private final String workflowName;

    /**
     * 工作流执行ID
     */
    private final String executionId;

    /**
     * 构造函数
     * @param source 事件源对象
     * @param workflowName 工作流名称
     * @param executionId 执行ID
     */
    protected WorkflowEvent(Object source, String workflowName, String executionId) {
        this.source = source;
        this.workflowName = workflowName;
        this.executionId = executionId;
    }
}
