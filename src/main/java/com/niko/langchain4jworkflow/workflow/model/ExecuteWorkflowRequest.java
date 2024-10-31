package com.niko.langchain4jworkflow.workflow.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 执行工作流请求对象
 * 包含执行工作流所需的所有参数信息
 */
@Data
public class ExecuteWorkflowRequest {
    /**
     * 工作流名称
     */
    @NotBlank(message = "Workflow name is required")
    private String workflowName;
    
    /**
     * 工作流输入参数
     */
    private Map<String, Object> inputs;
    
    /**
     * 执行上下文参数
     */
    private Map<String, Object> context;
    
    /**
     * 执行配置信息
     */
    private WorkflowConfigRequest config;
}