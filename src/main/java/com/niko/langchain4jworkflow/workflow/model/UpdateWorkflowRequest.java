package com.niko.langchain4jworkflow.workflow.model;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 更新工作流请求对象
 * 包含更新现有工作流所需的所有参数信息
 */
@Data
public class UpdateWorkflowRequest {
    /**
     * 工作流名称
     */
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Invalid name format")
    private String name;
    
    /**
     * 工作流描述
     */
    private String description;
    
    /**
     * 工作流节点列表
     */
    private List<CreateNodeRequest> nodes;
    
    /**
     * 工作流配置信息
     */
    private WorkflowConfigRequest config;
    
    /**
     * 工作流元数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 工作流版本
     */
    private String version;
    
    /**
     * 是否强制更新
     */
    private boolean force;
}
