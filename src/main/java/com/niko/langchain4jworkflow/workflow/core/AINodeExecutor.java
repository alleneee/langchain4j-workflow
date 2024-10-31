package com.niko.langchain4jworkflow.workflow.core;

import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class AINodeExecutor implements NodeExecutor {
    private final ChatLanguageModel chatModel;
    private final DefaultNodeExecutor defaultExecutor;

    @Override
    public CompletableFuture<WorkflowState> execute(
            Node node,
            WorkflowState state,
            WorkflowContext context) {

        if (node.getType() != NodeType.AI) {
            return defaultExecutor.execute(node, state, context);
        }

        try {
            state.recordNodeStart(node.getName());

            // 构建 AI 提示
            String prompt = buildPrompt(node, state);

            // 异步执行 AI 调用
            return CompletableFuture.supplyAsync(() -> {
                String response = chatModel.generate(prompt);
                Map<String, Object> outputs = new HashMap<>();
                outputs.put("response", response);
                state.recordNodeCompletion(node.getName(), outputs);
                return state;
            });

        } catch (Exception e) {
            state.recordNodeError(node.getName(), e);
            CompletableFuture<WorkflowState> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private String buildPrompt(Node node, WorkflowState state) {
        StringBuilder prompt = new StringBuilder();

        // 添加系统提示
        if (node.getConfig().getSystemPrompt() != null) {
            prompt.append(node.getConfig().getSystemPrompt())
                    .append("\n\n");
        }

        // 添加上下文变量
        node.getInputs().forEach((key, type) -> {
            Object value = state.getVariable(key);
            if (value != null) {
                prompt.append(key)
                        .append(": ")
                        .append(value)
                        .append("\n");
            }
        });

        return prompt.toString();
    }
}
