package com.niko.langchain4jworkflow.workflow.core;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 工作流引擎接口
 */
public interface WorkflowEngine {
    /**
     * 执行工作流
     *
     * @param workflowName 工作流名称
     * @param inputs 输入参数
     * @return 工作流执行状态的Future
     */
    CompletableFuture<WorkflowState> execute(String workflowName, Map<String, Object> inputs);

    /**
     * 停止工作流执行
     *
     * @param executionId 执行ID
     */
    void stop(String executionId);

    /**
     * 获取工作流执行状态
     *
     * @param executionId 执行ID
     * @return 工作流状态
     */
    WorkflowState getExecutionState(String executionId);

    /**
     * 注册工作流
     *
     * @param workflowBean 工作流实例
     */
    void register(Object workflowBean);

    /**
     * 执行工作流（简化版本）
     *
     * @param workflowName 工作流名称
     * @param args 参数列表
     */
    void execute(String workflowName, Object... args);
}
