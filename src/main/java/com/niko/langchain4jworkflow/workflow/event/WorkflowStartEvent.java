package com.niko.langchain4jworkflow.workflow.event;

import lombok.Getter;
import java.util.Map;

/**
 * 工作流开始事件
 * 在工作流实例开始执行时触发，包含工作流的输入参数信息
 */
@Getter
public class WorkflowStartEvent extends WorkflowEvent {
    /**
     * 工作流输入参数
     */
    private final Map<String, Object> inputs;

    /**
     * 构造函数
     * @param source 事件源对象
     * @param workflowName 工作流名称
     * @param executionId 执行ID
     * @param inputs 输入参数
     */
    public WorkflowStartEvent(Object source, String workflowName, String executionId, Map<String, Object> inputs) {
        super(source, workflowName, executionId);
        this.inputs = inputs;
    }
}

