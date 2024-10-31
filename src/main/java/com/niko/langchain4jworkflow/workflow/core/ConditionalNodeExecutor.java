package com.niko.langchain4jworkflow.workflow.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class ConditionalNodeExecutor implements NodeExecutor {
    private final SpelExpressionParser parser = new SpelExpressionParser();
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
            Boolean result = evaluateCondition(node, state);
            state.setVariable(node.getName() + "_result", result);

            state.recordNodeCompletion(node.getName());
            return CompletableFuture.completedFuture(state);

        } catch (Exception e) {
            state.recordNodeError(node.getName(), e);
            CompletableFuture<WorkflowState> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private Boolean evaluateCondition(Node node, WorkflowState state) {
        String condition = node.getConfig().getSystemPrompt(); // 使用systemPrompt字段存储条件表达式
        if (condition == null || condition.isEmpty()) {
            return true;
        }

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("state", state);
        context.setVariable("variables", state.getVariables());

        Expression expression = parser.parseExpression(condition);
        return expression.getValue(context, Boolean.class);
    }
}
