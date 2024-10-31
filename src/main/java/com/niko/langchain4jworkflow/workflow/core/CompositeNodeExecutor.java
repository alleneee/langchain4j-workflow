package com.niko.langchain4jworkflow.workflow.core;

import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class CompositeNodeExecutor implements NodeExecutor {
    private final Map<NodeType, NodeExecutor> executors;
    private final DefaultNodeExecutor defaultExecutor;

    public CompositeNodeExecutor(
            ChatLanguageModel chatModel,
            DefaultNodeExecutor defaultExecutor) {

        this.defaultExecutor = defaultExecutor;
        this.executors = new EnumMap<>(NodeType.class);

        // 注册各种节点执行器
        executors.put(NodeType.AI, new AINodeExecutor(chatModel, defaultExecutor));
        executors.put(NodeType.CONDITIONAL, new ConditionalNodeExecutor(defaultExecutor));
        executors.put(NodeType.PARALLEL, new ParallelNodeExecutor(defaultExecutor));
    }

    @Override
    public CompletableFuture<WorkflowState> execute(
            Node node,
            WorkflowState state,
            WorkflowContext context) {

        NodeExecutor executor = executors.getOrDefault(
                node.getType(), defaultExecutor);

        return executor.execute(node, state, context);
    }
}