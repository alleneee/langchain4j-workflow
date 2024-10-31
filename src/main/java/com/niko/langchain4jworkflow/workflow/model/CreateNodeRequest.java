package com.niko.langchain4jworkflow.workflow.model;

import com.niko.langchain4jworkflow.workflow.core.NodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 创建节点请求对象
 * 包含创建新节点所需的所有参数信息
 */
@Data
@Builder
public class CreateNodeRequest {
    /**
     * 节点名称
     */
    @NotBlank(message = "Node name is required")
    private String name;

    /**
     * 节点描述
     */
    private String description;

    /**
     * 节点类型
     */
    @NotNull(message = "Node type is required")
    private NodeType type;

    /**
     * 节点依赖列表
     */
    private List<String> dependencies;

    /**
     * 节点配置信息
     */
    private NodeConfigRequest config;

    /**
     * 节点输入参数映射
     */
    private Map<String, String> inputs;

    /**
     * 节点输出参数映射
     */
    private Map<String, String> outputs;

    /**
     * 节点元数据
     */
    private Map<String, Object> metadata;

    /**
     * 子节点列表（用于PARALLEL类型节点）
     */
    private List<CreateNodeRequest> nodes;
}
