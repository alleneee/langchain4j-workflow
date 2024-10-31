package com.niko.langchain4jworkflow.workflow.service;

import com.niko.langchain4jworkflow.workflow.exceptions.*;
import com.niko.langchain4jworkflow.workflow.model.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 工作流服务接口
 * 提供工作流的创建、管理、执行等核心功能
 */
public interface WorkflowService {

    /**
     * 创建新的工作流
     * 
     * @param request 创建工作流请求，包含工作流定义和配置信息
     * @return 创建的工作流DTO对象
     * @throws WorkflowAlreadyExistsException 当工作流名称已存在时
     * @throws WorkflowCreationException 当工作流创建失败时
     */
    WorkflowDTO createWorkflow(CreateWorkflowRequest request);
    
    /**
     * 更新现有工作流
     * 
     * @param id 工作流ID
     * @param request 更新工作流请求，包含需要更新的工作流信息
     * @return 更新后的工作流DTO对象
     * @throws WorkflowNotFoundException 当指定ID的工作流不存在时
     * @throws WorkflowUpdateException 当工作流更新失败时
     */
    WorkflowDTO updateWorkflow(String id, UpdateWorkflowRequest request);
    
    /**
     * 删除工作流
     * 
     * @param id 要删除的工作流ID
     * @throws WorkflowNotFoundException 当指定ID的工作流不存在时
     * @throws WorkflowInUseException 当工作流正在被使用时
     */
    void deleteWorkflow(String id);
    
    /**
     * 获取工作流详情
     * 
     * @param id 工作流ID
     * @return 工作流DTO对象
     * @throws WorkflowNotFoundException 当指定ID的工作流不存在时
     */
    WorkflowDTO getWorkflow(String id);
    
    /**
     * 获取所有工作流列表
     * 
     * @return 工作流DTO对象列表
     */
    List<WorkflowDTO> listWorkflows();
    
    /**
     * 执行工作流
     * 
     * @param request 执行工作流请求，包含执行参数和上下文信息
     * @return 工作流执行响应，包含执行状态和结果
     * @throws WorkflowNotFoundException 当指定的工作流不存在时
     * @throws WorkflowExecutionException 当工作流执行失败时
     */
    WorkflowResponse executeWorkflow(ExecuteWorkflowRequest request);
    
    /**
     * 停止工作流执行
     * 
     * @param executionId 执行ID
     * @throws ExecutionNotFoundException 当指定的执行ID不存在时
     */
    void stopWorkflow(String executionId);
    
    /**
     * 获取工作流执行状态
     * 
     * @param executionId 执行ID
     * @return 工作流执行响应，包含当前执行状态和结果
     * @throws ExecutionNotFoundException 当指定的执行ID不存在时
     */
    WorkflowResponse getExecutionStatus(String executionId);
}