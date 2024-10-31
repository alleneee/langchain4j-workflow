package com.niko.langchain4jworkflow.workflow.core;

import com.niko.langchain4jworkflow.workflow.event.*;
import com.niko.langchain4jworkflow.workflow.exceptions.*;
import com.niko.langchain4jworkflow.workflow.metrics.MetricsRegistry;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Slf4j
@Builder
public class DefaultWorkflowEngine implements WorkflowEngine {
    private final WorkflowRegistry workflowRegistry;
    private final NodeExecutor nodeExecutor;
    private final MetricsRegistry metricsRegistry;
    private final ChatLanguageModel chatModel;
    private final ApplicationEventPublisher eventPublisher;
    private final ExecutorService executorService;

    // 存储活跃的工作流执行状态
    private final Map<String, ExecutionContext> activeExecutions = new ConcurrentHashMap<>();

    @Builder.Default
    private final Duration defaultTimeout = Duration.ofMinutes(5);

    @Override
    public CompletableFuture<WorkflowState> execute(
            String workflowName,
            Map<String, Object> inputs) {

        log.info("Starting workflow execution: {}", workflowName);

        try {
            // 获取工作流定义
            WorkflowDefinition workflow = workflowRegistry.get(workflowName)
                    .orElseThrow(() -> new WorkflowNotFoundException(workflowName));

            // 创建执行上下文
            String executionId = generateExecutionId();
            WorkflowState state = new WorkflowState(executionId);
            state.getVariables().putAll(inputs);

            ExecutionContext context = ExecutionContext.builder()
                    .workflow(workflow)
                    .state(state)
                    .build();

            // 记录活跃执行
            activeExecutions.put(executionId, context);

            // 发布工作流开始事件
            publishWorkflowStartEvent(workflowName, executionId, inputs);

            // 开始执行
            return executeWorkflow(context)
                    .thenApply(finalState -> {
                        handleSuccess(context);
                        return finalState;
                    })
                    .exceptionally(throwable -> {
                        handleError(context, throwable);
                        throw new CompletionException(throwable);
                    })
                    .whenComplete((result, error) ->
                            activeExecutions.remove(executionId));

        } catch (Exception e) {
            log.error("Failed to start workflow execution: {}", workflowName, e);
            throw new WorkflowExecutionException(
                    "Failed to start workflow: " + e.getMessage(), e);
        }
    }

    @Override
    public void stop(String executionId) {
        ExecutionContext context = activeExecutions.get(executionId);
        if (context == null) {
            throw new ExecutionNotFoundException(executionId);
        }

        try {
            context.cancel();
            activeExecutions.remove(executionId);

            log.info("Stopped workflow execution: {}", executionId);

            // 发布工作流取消事件
            publishWorkflowCancelEvent(context.getWorkflow().getName(), executionId);

        } catch (Exception e) {
            log.error("Failed to stop workflow execution: {}", executionId, e);
            throw new WorkflowExecutionException(
                    "Failed to stop workflow: " + e.getMessage(), e);
        }
    }

    @Override
    public WorkflowState getExecutionState(String executionId) {
        ExecutionContext context = activeExecutions.get(executionId);
        if (context == null) {
            throw new ExecutionNotFoundException(executionId);
        }
        return context.getState();
    }

    private CompletableFuture<WorkflowState> executeWorkflow(
            ExecutionContext context) {

        try {
            // 获取起始节点
            List<String> startNodes = context.getWorkflow().getStartNodes();
            if (startNodes.isEmpty()) {
                throw new InvalidWorkflowException("No start nodes defined");
            }

            // 并行执行所有起始节点
            List<CompletableFuture<WorkflowState>> futures = new ArrayList<>();
            for (String nodeName : startNodes) {
                Node node = context.getWorkflow().getNodes().get(nodeName);
                futures.add(executeNode(node, context));
            }

            // 等待所有节点完成
            return CompletableFuture.allOf(
                            futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> mergeStates(context.getState(), futures));

        } catch (Exception e) {
            log.error("Workflow execution failed", e);
            throw new WorkflowExecutionException(
                    "Workflow execution failed: " + e.getMessage(), e);
        }
    }

    private CompletableFuture<WorkflowState> executeNode(
            Node node,
            ExecutionContext context) {

        try {
            // 检查节点是否可以执行
            if (!canExecuteNode(node, context.getState())) {
                return CompletableFuture.completedFuture(context.getState());
            }

            // 记录节点开始
            publishNodeStartEvent(
                    context.getWorkflow().getName(),
                    context.getState().getWorkflowId(),
                    node.getName());

            // 执行节点
            return nodeExecutor.execute(node, context.getState(),
                            buildWorkflowContext(context))
                    .thenCompose(newState -> executeNextNodes(node, context, newState))
                    .whenComplete((state, error) -> {
                        if (error != null) {
                            handleNodeError(node, context, error);
                        } else {
                            handleNodeSuccess(node, context, state);
                        }
                    });

        } catch (Exception e) {
            log.error("Node execution failed: {}", node.getName(), e);
            throw new NodeExecutionException(
                    node.getName(), "Node execution failed", e);
        }
    }

    private boolean canExecuteNode(Node node, WorkflowState state) {
        // 检查依赖节点是否都已完成
        if (node.getDependencies() != null) {
            return node.getDependencies().stream()
                    .allMatch(state::isNodeCompleted);
        }
        return true;
    }

    private CompletableFuture<WorkflowState> executeNextNodes(
            Node currentNode,
            ExecutionContext context,
            WorkflowState state) {

        // 找到所有依赖当前节点的后续节点
        List<Node> nextNodes = findNextNodes(currentNode, context.getWorkflow());
        if (nextNodes.isEmpty()) {
            return CompletableFuture.completedFuture(state);
        }

        // 并行执行所有后续节点
        List<CompletableFuture<WorkflowState>> futures = new ArrayList<>();
        for (Node node : nextNodes) {
            ExecutionContext nextContext = context.withState(state);
            futures.add(executeNode(node, nextContext));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> mergeStates(state, futures));
    }

    private List<Node> findNextNodes(Node currentNode, WorkflowDefinition workflow) {
        return workflow.getNodes().values().stream()
                .filter(node -> node.getDependencies() != null &&
                        node.getDependencies().contains(currentNode.getName()))
                .toList();
    }

    private WorkflowState mergeStates(
            WorkflowState baseState,
            List<CompletableFuture<WorkflowState>> futures) {
        futures.stream()
                .map(CompletableFuture::join)
                .forEach(state -> {
                    baseState.getVariables().putAll(state.getVariables());
                    baseState.getExecutionHistory().putAll(state.getExecutionHistory());
                });
        return baseState;
    }

    private WorkflowContext buildWorkflowContext(ExecutionContext context) {
        return WorkflowContext.builder()
                .workflowId(context.getState().getWorkflowId())
                .workflowName(context.getWorkflow().getName())
                .state(context.getState())
                .build();
    }

    private void handleSuccess(ExecutionContext context) {
        String workflowName = context.getWorkflow().getName();
        String executionId = context.getState().getWorkflowId();

        // 更新状态
        context.getState().markAsCompleted();

        // 记录指标
        metricsRegistry.incrementCounter(
                "workflow.completions",
                "workflow", workflowName);

        // 发布完成事件
        publishWorkflowCompleteEvent(workflowName, executionId, context.getState());

        log.info("Workflow completed successfully: {} ({})",
                workflowName, executionId);
    }

    private void handleError(ExecutionContext context, Throwable error) {
        String workflowName = context.getWorkflow().getName();
        String executionId = context.getState().getWorkflowId();

        // 更新状态
        context.getState().markAsFailed(error.getMessage());

        // 记录指标
        metricsRegistry.incrementCounter(
                "workflow.failures",
                "workflow", workflowName,
                "error", error.getClass().getSimpleName());

        // 发布错误事件
        publishWorkflowErrorEvent(workflowName, executionId, error);

        log.error("Workflow execution failed: {} ({})",
                workflowName, executionId, error);
    }

    private void handleNodeSuccess(
            Node node,
            ExecutionContext context,
            WorkflowState state) {

        // 记录指标
        metricsRegistry.incrementCounter(
                "node.completions",
                "workflow", context.getWorkflow().getName(),
                "node", node.getName());

        // 发布节点完成事件
        publishNodeCompleteEvent(
                context.getWorkflow().getName(),
                context.getState().getWorkflowId(),
                node.getName(),
                state);
    }

    private void handleNodeError(
            Node node,
            ExecutionContext context,
            Throwable error) {

        // 记录指标
        metricsRegistry.incrementCounter(
                "node.failures",
                "workflow", context.getWorkflow().getName(),
                "node", node.getName(),
                "error", error.getClass().getSimpleName());

        // 发布节点错误事件
        publishNodeErrorEvent(
                context.getWorkflow().getName(),
                context.getState().getWorkflowId(),
                node.getName(),
                error);
    }

    private void publishEvent(Object event) {
        if (eventPublisher != null) {
            eventPublisher.publishEvent(event);
        }
    }

    private void publishWorkflowStartEvent(String workflowName, String executionId, Map<String, Object> inputs) {
        publishEvent(new WorkflowStartEvent(this, workflowName, executionId, inputs));
    }

    private void publishWorkflowCompleteEvent(String workflowName, String executionId, WorkflowState state) {
        publishEvent(new WorkflowCompleteEvent(this, workflowName, executionId, state));
    }

    private void publishWorkflowErrorEvent(String workflowName, String executionId, Throwable error) {
        publishEvent(new WorkflowErrorEvent(this, workflowName, executionId, error));
    }

    private void publishWorkflowCancelEvent(String workflowName, String executionId) {
        publishEvent(new WorkflowCancelEvent(this, workflowName, executionId));
    }

    private void publishNodeStartEvent(String workflowName, String executionId, String nodeName) {
        publishEvent(new NodeStartEvent(this, workflowName, executionId, nodeName));
    }

    private void publishNodeCompleteEvent(String workflowName, String executionId, String nodeName, WorkflowState state) {
        publishEvent(new NodeCompleteEvent(this, workflowName, executionId, nodeName, state));
    }

    private void publishNodeErrorEvent(String workflowName, String executionId, String nodeName, Throwable error) {
        publishEvent(new NodeErrorEvent(this, workflowName, executionId, nodeName, error));
    }

    private String generateExecutionId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void execute(String workflowName, Object... args) {

    }

    @Override
    public void register(Object workflowBean) {

    }

    @Builder
    private static class ExecutionContext {
        private final WorkflowDefinition workflow;
        private final WorkflowState state;
        private final CompletableFuture<Void> cancellation;

        public WorkflowDefinition getWorkflow() {
            return workflow;
        }

        public WorkflowState getState() {
            return state;
        }

        public ExecutionContext withState(WorkflowState newState) {
            return ExecutionContext.builder()
                    .workflow(workflow)
                    .state(newState)
                    .cancellation(cancellation)
                    .build();
        }

        public void cancel() {
            if (cancellation != null) {
                cancellation.cancel(true);
            }
        }
    }
}
