package com.niko.langchain4jworkflow.workflow.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ParallelNodeExecutor implements NodeExecutor {
    private final DefaultNodeExecutor defaultExecutor;

    @Override
    public CompletableFuture<WorkflowState> execute(
            Node node,
            WorkflowState state,
            WorkflowContext context) {

        if (node.getType() != NodeType.PARALLEL) {
            return defaultExecutor.execute(node, state, context);
        }

        try {
            state.recordNodeStart(node.getName());

            // 获取所有并行节点
            List<Node> parallelNodes = getParallelNodes(node);

            // 并行执行所有节点
            List<CompletableFuture<WorkflowState>> futures = parallelNodes.stream()
                    .map(n -> defaultExecutor.execute(n, state.clone(), context))
                    .collect(Collectors.toList());

            // 等待所有节点完成并合并结果
            return CompletableFuture.allOf(
                            futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> mergeResults(state, futures));

        } catch (Exception e) {
            state.recordNodeError(node.getName(), e);
            CompletableFuture<WorkflowState> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private List<Node> getParallelNodes(Node node) {
        // TODO: 从节点配置中获取并行节点列表
        return new ArrayList<>();
    }

    private WorkflowState mergeResults(
            WorkflowState originalState,
            List<CompletableFuture<WorkflowState>> futures) {

        Map<String, Object> mergedOutputs = new HashMap<>();
        futures.stream()
                .map(CompletableFuture::join)
                .forEach(state -> {
                    mergedOutputs.putAll(state.getVariables());
                    originalState.getExecutionHistory()
                            .putAll(state.getExecutionHistory());
                });

        originalState.recordNodeCompletion(originalState.getWorkflowId(), mergedOutputs);
        return originalState;
    }
}