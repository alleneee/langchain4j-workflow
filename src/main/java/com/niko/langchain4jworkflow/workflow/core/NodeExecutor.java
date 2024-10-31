package com.niko.langchain4jworkflow.workflow.core;

import java.util.concurrent.CompletableFuture;

/**
 * 节点执行器接口
 * 定义了节点执行的核心行为，负责执行单个工作流节点
 */
public interface NodeExecutor {
    
    /**
     * 执行节点
     * 
     * @param node 要执行的节点
     * @param state 当前工作流状态
     * @param context 工作流上下文
     * @return 包含更新后工作流状态的Future对象
     */
    CompletableFuture<WorkflowState> execute(
            Node node,
            WorkflowState state,
            WorkflowContext context);
}
