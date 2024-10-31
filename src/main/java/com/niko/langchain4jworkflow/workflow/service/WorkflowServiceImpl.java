package com.niko.langchain4jworkflow.workflow.service;

import com.niko.langchain4jworkflow.workflow.core.*;
import com.niko.langchain4jworkflow.workflow.exceptions.WorkflowExecutionException;
import com.niko.langchain4jworkflow.workflow.exceptions.WorkflowNotFoundException;
import com.niko.langchain4jworkflow.workflow.metrics.MetricsRegistry;
import com.niko.langchain4jworkflow.workflow.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {
    private final WorkflowRegistry workflowRegistry;
    private final WorkflowEngine workflowEngine;
    private final WorkflowValidator validator;
    private final MetricsRegistry metricsRegistry;
    private final WorkflowMapper mapper;

    // 存储执行中的工作流状态
    private final Map<String, CompletableFuture<WorkflowState>> activeExecutions =
            new ConcurrentHashMap<>();

    @Override
    @Transactional
    public WorkflowDTO createWorkflow(CreateWorkflowRequest request) {
        log.info("Creating workflow: {}", request.getName());

        // 验证请求
        validator.validateCreateRequest(request);

        // 检查工作流名称是否已存在
        if (workflowRegistry.exists(request.getName())) {
            throw new WorkflowAlreadyExistsException(request.getName());
        }

        try {
            // 构建工作流定义
            WorkflowDefinition workflow = buildWorkflowDefinition(request);

            // 注册工作流
            workflowRegistry.register(workflow);

            // 转换为DTO
            WorkflowDTO dto = mapper.toDto(workflow);

            log.info("Successfully created workflow: {}", request.getName());
            return dto;

        } catch (Exception e) {
            log.error("Failed to create workflow: {}", request.getName(), e);
            throw new WorkflowCreationException(
                    "Failed to create workflow: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public WorkflowDTO updateWorkflow(String id, UpdateWorkflowRequest request) {
        log.info("Updating workflow: {}", id);

        // 获取现有工作流
        WorkflowDefinition existing = workflowRegistry.get(id)
                .orElseThrow(() -> new WorkflowNotFoundException(id));

        try {
            // 更新工作流定义
            WorkflowDefinition updated = updateWorkflowDefinition(
                    existing, request);

            // 重新注册工作流
            workflowRegistry.register(updated);

            // 转换为DTO
            WorkflowDTO dto = mapper.toDto(updated);

            log.info("Successfully updated workflow: {}", id);
            return dto;

        } catch (Exception e) {
            log.error("Failed to update workflow: {}", id, e);
            throw new WorkflowUpdateException(
                    "Failed to update workflow: " + e.getMessage(), e);
        }
    }
    @Transactional
    @Override
    public void deleteWorkflow(String id) {
        log.info("Deleting workflow: {}", id);

        // 检查是否有正在执行的实例
        if (hasActiveExecutions(id)) {
            throw new WorkflowInUseException(
                    "Cannot delete workflow with active executions");
        }

        try {
            workflowRegistry.unregister(id);
            log.info("Successfully deleted workflow: {}", id);

        } catch (Exception e) {
            log.error("Failed to delete workflow: {}", id, e);
            throw new WorkflowDeletionException(
                    "Failed to delete workflow: " + e.getMessage(), e);
        }
    }

    @Override
    public WorkflowResponse executeWorkflow(ExecuteWorkflowRequest request) {
        log.info("Executing workflow: {}", request.getWorkflowName());

        // 生成执行ID
        String executionId = generateExecutionId();

        try {
            // 准备执行上下文
            Map<String, Object> context = new HashMap<>(request.getInputs());
            if (request.getContext() != null) {
                context.putAll(request.getContext());
            }

            // 开始执行
            CompletableFuture<WorkflowState> future =
                    workflowEngine.execute(request.getWorkflowName(), context);

            // 记录执行状态
            activeExecutions.put(executionId, future);

            // 添加完成回调
            future.whenComplete((state, error) -> {
                activeExecutions.remove(executionId);
                if (error != null) {
                    log.error("Workflow execution failed: {}",
                            request.getWorkflowName(), error);
                }
            });

            // 返回初始响应
            return buildInitialResponse(executionId, request.getWorkflowName());

        } catch (Exception e) {
            log.error("Failed to start workflow execution: {}",
                    request.getWorkflowName(), e);
            throw new WorkflowExecutionException(
                    "Failed to start workflow execution: " + e.getMessage(), e);
        }
    }

    @Override
    public void stopWorkflow(String executionId) {
        log.info("Stopping workflow execution: {}", executionId);

        CompletableFuture<WorkflowState> future =
                activeExecutions.get(executionId);
        if (future == null) {
            throw new ExecutionNotFoundException(executionId);
        }

        try {
            future.cancel(true);
            activeExecutions.remove(executionId);
            log.info("Successfully stopped workflow execution: {}", executionId);

        } catch (Exception e) {
            log.error("Failed to stop workflow execution: {}", executionId, e);
            throw new WorkflowExecutionException(
                    "Failed to stop workflow execution: " + e.getMessage(), e);
        }
    }

    @Override
    public WorkflowResponse getExecutionStatus(String executionId) {
        CompletableFuture<WorkflowState> future =
                activeExecutions.get(executionId);
        if (future == null) {
            throw new ExecutionNotFoundException(executionId);
        }

        try {
            if (future.isDone()) {
                if (future.isCompletedExceptionally()) {
                    return buildErrorResponse(executionId, future.join());
                } else {
                    return buildSuccessResponse(executionId, future.join());
                }
            } else {
                return buildInProgressResponse(executionId, future);
            }
        } catch (Exception e) {
            log.error("Failed to get execution status: {}", executionId, e);
            throw new WorkflowExecutionException(
                    "Failed to get execution status: " + e.getMessage(), e);
        }
    }

    private boolean hasActiveExecutions(String workflowId) {
        return activeExecutions.values().stream()
                .anyMatch(future -> !future.isDone() &&
                        getWorkflowId(future).equals(workflowId));
    }

    private String generateExecutionId() {
        return UUID.randomUUID().toString();
    }

    private WorkflowDefinition buildWorkflowDefinition(
            CreateWorkflowRequest request) {
        return WorkflowDefinition.builder()
                .name(request.getName())
                .description(request.getDescription())
                .nodes(buildNodes(request.getNodes()))
                .config(buildConfig(request.getConfig()))
                .metadata(request.getMetadata())
                .build();
    }

    private Map<String, Node> buildNodes(List<CreateNodeRequest> nodeRequests) {
        Map<String, Node> nodes = new HashMap<>();
        for (CreateNodeRequest nodeRequest : nodeRequests) {
            nodes.put(nodeRequest.getName(), buildNode(nodeRequest));
        }
        return nodes;
    }

    private Node buildNode(CreateNodeRequest request) {
        return Node.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .dependencies(request.getDependencies())
                .config(buildNodeConfig(request.getConfig()))
                .inputs(request.getInputs())
                .outputs(request.getOutputs())
                .metadata(request.getMetadata())
                .build();
    }

    private WorkflowResponse buildInitialResponse(
            String executionId,
            String workflowName) {
        return WorkflowResponse.builder()
                .workflowId(workflowName)
                .executionId(executionId)
                .status("RUNNING")
                .startTime(Instant.now())
                .build();
    }

    private WorkflowResponse buildSuccessResponse(
            String executionId,
            WorkflowState state) {
        return WorkflowResponse.builder()
                .workflowId(state.getWorkflowId())
                .executionId(executionId)
                .status("COMPLETED")
                .outputs(state.getVariables())
                .nodeExecutions(buildNodeExecutions(state))
                .metrics(buildMetrics(state))
                .startTime(state.getStartTime())
                .endTime(state.getEndTime())
                .duration(state.getDuration())
                .build();
    }

    private WorkflowResponse buildErrorResponse(
            String executionId,
            WorkflowState state) {
        return WorkflowResponse.builder()
                .workflowId(state.getWorkflowId())
                .executionId(executionId)
                .status("FAILED")
                .nodeExecutions(buildNodeExecutions(state))
                .metrics(buildMetrics(state))
                .startTime(state.getStartTime())
                .endTime(state.getEndTime())
                .duration(state.getDuration())
                .build();
    }

    private Map<String, NodeExecutionResponse> buildNodeExecutions(
            WorkflowState state) {
        Map<String, NodeExecutionResponse> executions = new HashMap<>();
        state.getExecutionHistory().forEach((nodeName, info) ->
                executions.put(nodeName, buildNodeExecution(info)));
        return executions;
    }

    private NodeExecutionResponse buildNodeExecution(
            WorkflowState.NodeExecutionInfo info) {
        return NodeExecutionResponse.builder()
                .nodeName(info.getNodeName())
                .status(info.getError() == null ? "COMPLETED" : "FAILED")
                .outputs(info.getOutputs())
                .attempts(info.getAttempts())
                .error(info.getError())
                .startTime(info.getStartTime())
                .endTime(info.getEndTime())
                .duration(info.getDuration())
                .build();
    }

    private ExecutionMetrics buildMetrics(WorkflowState state) {
        return ExecutionMetrics.builder()
                .totalNodes(state.getExecutionHistory().size())
                .completedNodes((int) state.getExecutionHistory().values().stream()
                        .filter(info -> info.getError() == null)
                        .count())
                .failedNodes((int) state.getExecutionHistory().values().stream()
                        .filter(info -> info.getError() != null)
                        .count())
                .totalRetries((int) state.getExecutionHistory().values().stream()
                        .mapToInt(info -> info.getAttempts() - 1)
                        .sum())
                .totalDuration(state.getDuration())
                .build();
    }
}