package com.niko.langchain4jworkflow.workflow.core;

import com.niko.langchain4jworkflow.workflow.exceptions.InvalidWorkflowException;
import com.niko.langchain4jworkflow.workflow.exceptions.WorkflowExecutionException;
import com.niko.langchain4jworkflow.workflow.exceptions.WorkflowNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Builder
@RequiredArgsConstructor
public class WorkflowExecutor {
    private final WorkflowRegistry registry;
    private final NodeExecutor nodeExecutor;
    private final ApplicationContext applicationContext;

    public CompletableFuture<WorkflowState> execute(
            String workflowName,
            Map<String, Object> inputs) {

        WorkflowDefinition workflow = registry.get(workflowName)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowName));

        // 创建工作流状态
        WorkflowState state = new WorkflowState();
        state.getVariables().putAll(inputs);

        // 创建工作流上下文
        WorkflowContext context = WorkflowContext.builder()
                .workflowId(state.getWorkflowId())
                .workflowName(workflowName)
                .applicationContext(applicationContext)
                .state(state)
                .build();

        return executeWorkflow(workflow, state, context);
    }

    private CompletableFuture<WorkflowState> executeWorkflow(
            WorkflowDefinition workflow,
            WorkflowState state,
            WorkflowContext context) {

        try {
            // 验证工作流配置
            validateWorkflow(workflow);

            // 执行开始节点
            List<CompletableFuture<WorkflowState>> futures =
                    executeStartNodes(workflow, state, context);

            // 等待所有节点执行完成
            return CompletableFuture.allOf(
                            futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        state.markAsCompleted();
                        return state;
                    })
                    .exceptionally(throwable -> {
                        state.markAsFailed(throwable.getMessage());
                        throw new WorkflowExecutionException(
                                "Workflow execution failed", throwable);
                    });

        } catch (Exception e) {
            state.markAsFailed(e.getMessage());
            CompletableFuture<WorkflowState> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private void validateWorkflow(WorkflowDefinition workflow) {
        if (workflow.getStartNodes() == null || workflow.getStartNodes().isEmpty()) {
            throw new InvalidWorkflowException("No start nodes defined");
        }
        validateNodes(workflow);
    }

    private void validateNodes(WorkflowDefinition workflow) {
        Map<String, Node> nodes = workflow.getNodes();
        for (Node node : nodes.values()) {
            // 验证节点依赖
            if (node.getDependencies() != null) {
                for (String dep : node.getDependencies()) {
                    if (!nodes.containsKey(dep)) {
                        throw new InvalidWorkflowException(
                                "Node " + node.getName() +
                                        " depends on non-existent node: " + dep);
                    }
                }
            }

            // 验证节点配置
            validateNodeConfig(node);
        }
    }

    private void validateNodeConfig(Node node) {
        Node.NodeConfig config = node.getConfig();
        if (config == null) {
            return;
        }

        if (node.getType() == NodeType.AI) {
            if (config.getSystemPrompt() == null ||
                    config.getSystemPrompt().isEmpty()) {
                throw new InvalidWorkflowException(
                        "AI node " + node.getName() +
                                " requires system prompt");
            }
        }
    }

    private List<CompletableFuture<WorkflowState>> executeStartNodes(
            WorkflowDefinition workflow,
            WorkflowState state,
            WorkflowContext context) {

        return workflow.getStartNodes().stream()
                .map(nodeName -> {
                    Node node = workflow.getNodes().get(nodeName);
                    return executeNode(node, state, context);
                })
                .collect(Collectors.toList());
    }

    private CompletableFuture<WorkflowState> executeNode(
            Node node,
            WorkflowState state,
            WorkflowContext context) {

        // 检查节点是否可以执行
        if (!node.canExecute(state)) {
            return CompletableFuture.completedFuture(state);
        }

        return nodeExecutor.execute(node, state, context)
                .thenCompose(newState ->
                        executeNextNodes(node, newState, context));
    }

    private CompletableFuture<WorkflowState> executeNextNodes(
            Node currentNode,
            WorkflowState state,
            WorkflowContext context) {

        WorkflowDefinition workflow = registry
                .get(context.getWorkflowName())
                .orElseThrow(() -> new WorkflowNotFoundException(
                        context.getWorkflowName()));

        // 查找依赖于当前节点的所有节点
        List<Node> nextNodes = findNextNodes(currentNode, workflow);

        if (nextNodes.isEmpty()) {
            return CompletableFuture.completedFuture(state);
        }

        // 并行执行所有后续节点
        List<CompletableFuture<WorkflowState>> futures = nextNodes.stream()
                .map(node -> executeNode(node, state, context))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(
                        futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.get(futures.size() - 1).join());
    }

    private List<Node> findNextNodes(
            Node currentNode,
            WorkflowDefinition workflow) {

        return workflow.getNodes().values().stream()
                .filter(node ->
                        node.getDependencies() != null &&
                                node.getDependencies().contains(currentNode.getName()))
                .collect(Collectors.toList());
    }
}