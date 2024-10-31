package com.niko.langchain4jworkflow.workflow.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 创建工作流请求对象
 * 包含创建新工作流所需的所有参数信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkflowRequest {
    /**
     * 工作流名称
     * 必须符合指定的命名规则
     */
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Invalid name format")
    private String name;

    /**
     * 工作流描述
     */
    private String description;

    /**
     * 工作流节点列表
     * 至少需要包含一个节点
     */
    @NotEmpty(message = "At least one node is required")
    private List<CreateNodeRequest> nodes;

    /**
     * 工作流配置信息
     */
    private WorkflowConfigRequest config;

    /**
     * 工作流元数据
     */
    private Map<String, Object> metadata;
}