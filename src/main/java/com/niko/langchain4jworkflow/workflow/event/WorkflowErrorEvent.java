package com.niko.langchain4jworkflow.workflow.event;

import lombok.Getter;

/**
 * 工作流错误事件
 * 在工作流执行过程中发生错误时触发，包含错误信息
 */
@Getter
public class WorkflowErrorEvent extends WorkflowEvent {
    /**
     * 错误信息
     */
    private final Throwable error;

    /**
     * 构造函数
     * @param source 事件源对象
     * @param workflowName 工作流名称
     * @param executionId 执行ID
     * @param error 错误信息
     */
    public WorkflowErrorEvent(Object source, String workflowName, String executionId, Throwable error) {
        super(source, workflowName, executionId);
        this.error = error;
    }
}
