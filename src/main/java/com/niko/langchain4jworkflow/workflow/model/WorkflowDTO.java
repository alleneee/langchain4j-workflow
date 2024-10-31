package com.niko.langchain4jworkflow.workflow.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 工作流数据传输对象
 * 用于在系统各层之间传递工作流信息
 */
@Data
@Builder
public class WorkflowDTO {
    /**
     * 工作流ID
     */
    private String id;
    
    /**
     * 工作流名称
     */
    private String name;
    
    /**
     * 工作流描述
     */
    private String description;
    
    /**
     * 工作流节点列表
     */
    private List<NodeDTO> nodes;
    
    /**
     * 工作流配置信息
     */
    private WorkflowConfigDTO config;
    
    /**
     * 工作流元数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 工作流版本
     */
    private String version;
    
    /**
     * 工作流状态
     */
    private String status;
}
