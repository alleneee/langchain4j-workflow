package com.niko.langchain4jworkflow.workflow.model;

import com.example.workflow.core.NodeType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class NodeDTO {
    private String id;
    private String name;
    private String description;
    private NodeType type;
    private List<String> dependencies;
    private NodeConfigDTO config;
    private Map<String, String> inputs;
    private Map<String, String> outputs;
    private Map<String, Object> metadata;
}
