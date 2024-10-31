package com.niko.langchain4jworkflow.workflow.event;

import com.niko.langchain4jworkflow.workflow.core.WorkflowState;
import lombok.Getter;

/**
 * 工作流完成事件
 * 在工作流实例成功完成执行时触发，包含工作流的最终状态信息
 */
@Getter
public class WorkflowCompleteEvent extends WorkflowEvent {
    /**
     * 工作流最终状态
     */
    private final WorkflowState state;

    /**
     * 构造函数
     * @param source 事件源对象
     * @param workflowName 工作流名称
     * @param executionId 执行ID
     * @param state 工作流状态
     */
    public WorkflowCompleteEvent(Object source, String workflowName, String executionId, WorkflowState state) {
        super(source, workflowName, executionId);
        this.state = state;
    }
}