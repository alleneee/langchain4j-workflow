package com.niko.langchain4jworkflow.workflow.core;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 工作流引擎接口
 * 定义了工作流执行的核心行为，负责工作流的整体生命周期管理
 */
public interface WorkflowEngine {
    /**
     * 执行工作流
     *
     * @param workflowName 工作流名称
     * @param inputs 工作流输入参数
     * @return 包含工作流执行状态的Future对象
     */
    CompletableFuture<WorkflowState> execute(
            String workflowName,
            Map<String, Object> inputs);

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
     * 执行工作流（基于注解的方法）
     *
     * @param workflowName 工作流名称
     * @param args 方法参数
     */
    void execute(String workflowName, Object... args);

    /**
     * 注册工作流Bean
     *
     * @param workflowBean 工作流Bean对象
     */
    void register(Object workflowBean);
}
