package com.niko.langchain4jworkflow.workflow.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class ConditionalNodeExecutor implements NodeExecutor {
    private final DefaultNodeExecutor defaultExecutor;

    @Override
    public CompletableFuture<WorkflowState> execute(
            Node node,
            WorkflowState state,
            WorkflowContext context) {

        if (node.getType() != NodeType.CONDITIONAL) {
            return defaultExecutor.execute(node, state, context);
        }

        try {
            state.recordNodeStart(node.getName());

            // 评估条件
            boolean conditionMet = evaluateCondition(node, state);

            if (!conditionMet) {
                // 如果条件不满足，记录完成并返回当前状态
                Map<String, Object> outputs = new HashMap<>();
                outputs.put("conditionMet", false);
                state.recordNodeCompletion(node.getName(), outputs);
                return CompletableFuture.completedFuture(state);
            }

            // 如果条件满足，执行节点
            return defaultExecutor.execute(node, state, context);

        } catch (Exception e) {
            state.recordNodeError(node.getName(), e);
            CompletableFuture<WorkflowState> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private boolean evaluateCondition(Node node, WorkflowState state) {
        // TODO: 实现条件评估逻辑
        return true;
    }
}
