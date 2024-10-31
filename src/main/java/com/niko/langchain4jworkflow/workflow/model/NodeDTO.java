package com.niko.langchain4jworkflow.workflow.model;

import com.niko.langchain4jworkflow.workflow.core.NodeType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 节点数据传输对象
 * 用于在系统各层之间传递节点信息
 */
@Data
@Builder
public class NodeDTO {
    /**
     * 节点ID
     */
    private String id;
    
    /**
     * 节点名称
     */
    private String name;
    
    /**
     * 节点描述
     */
    private String description;
    
    /**
     * 节点类型
     */
    private NodeType type;
    
    /**
     * 节点依赖列表
     */
    private List<String> dependencies;
    
    /**
     * 节点配置信息
     */
    private NodeConfigDTO config;
    
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
}
