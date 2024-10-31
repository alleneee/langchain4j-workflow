package com.niko.langchain4jworkflow.workflow.core;

import com.niko.langchain4jworkflow.workflow.annotation.StateVariable;
import com.niko.langchain4jworkflow.workflow.exceptions.NodeExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public class DefaultNodeExecutor implements NodeExecutor {
    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer =
            new DefaultParameterNameDiscoverer();

    @Override
    public CompletableFuture<WorkflowState> execute(
            Node node,
            WorkflowState state,
            WorkflowContext context) {

        try {
            state.recordNodeStart(node.getName());
            Method method = node.getMethod();
            Object[] args = resolveArguments(method, state, context);

            CompletableFuture<Object> resultFuture;
            if (node.getConfig().isAsync()) {
                resultFuture = executeAsync(method, node.getTarget(), args);
            } else {
                resultFuture = CompletableFuture.completedFuture(
                        method.invoke(node.getTarget(), args));
            }

            return resultFuture
                    .thenApply(result -> processResult(node, state, result))
                    .exceptionally(throwable -> {
                        handleError(node, state, throwable);
                        throw new NodeExecutionException(
                                node.getName(), "Execution failed", throwable);
                    });

        } catch (Exception e) {
            handleError(node, state, e);
            CompletableFuture<WorkflowState> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private Object[] resolveArguments(
            Method method,
            WorkflowState state,
            WorkflowContext context) {

        Parameter[] parameters = method.getParameters();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            String paramName = parameterNames[i];
            args[i] = resolveParameter(param, paramName, state, context);
        }

        return args;
    }

    private Object resolveParameter(
            Parameter parameter,
            String parameterName,
            WorkflowState state,
            WorkflowContext context) {

        if (parameter.getType().equals(WorkflowState.class)) {
            return state;
        }
        if (parameter.getType().equals(WorkflowContext.class)) {
            return context;
        }

        // 处理@StateVariable注解
        StateVariable stateVar = parameter.getAnnotation(StateVariable.class);
        if (stateVar != null) {
            String key = stateVar.value().isEmpty() ? parameterName : stateVar.value();
            Object value = state.getVariable(key);
            if (value == null && stateVar.required()) {
                throw new IllegalArgumentException(
                        "Required state variable not found: " + key);
            }
            return value;
        }

        // 处理SpEL表达式
        String spelExpression = resolveSpelExpression(parameter);
        if (spelExpression != null) {
            EvaluationContext evalContext = createEvaluationContext(state, context);
            Expression expression = parser.parseExpression(spelExpression);
            return expression.getValue(evalContext, parameter.getType());
        }

        return null;
    }

    private CompletableFuture<Object> executeAsync(
            Method method,
            Object target,
            Object[] args) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return method.invoke(target, args);
            } catch (Exception e) {
                throw new NodeExecutionException(
                        "Async execution failed", e.getMessage());
            }
        });
    }

    private WorkflowState processResult(
            Node node,
            WorkflowState state,
            Object result) {
        if (result != null) {
            state.setVariable(node.getName() + "_result", result);
        }
        state.recordNodeCompletion(node.getName());
        return state;
    }

    private void handleError(
            Node node,
            WorkflowState state,
            Throwable error) {
        log.error("Node execution failed: {}", node.getName(), error);
        state.recordNodeError(// 继续 DefaultNodeExecutor.java 的 handleError 方法
                node.getName(), error);
        if (error instanceof RuntimeException) {
            throw (RuntimeException) error;
        }
        throw new NodeExecutionException(
                node.getName(), "Execution failed", error);
    }

    private String resolveSpelExpression(Parameter parameter) {
        // TODO: 添加自定义注解处理
        return null;
    }

    private EvaluationContext createEvaluationContext(
            WorkflowState state,
            WorkflowContext context) {
        StandardEvaluationContext evalContext = new StandardEvaluationContext();
        evalContext.setVariable("state", state);
        evalContext.setVariable("context", context);
        evalContext.setVariable("variables", state.getVariables());
        return evalContext;
    }
}