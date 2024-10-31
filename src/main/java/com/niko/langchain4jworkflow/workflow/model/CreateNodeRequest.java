package com.niko.langchain4jworkflow.workflow.model;

import com.niko.langchain4jworkflow.workflow.core.NodeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CreateNodeRequest {
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Invalid name format")
    private String name;

    private String description;

    @NotNull(message = "Node type is required")
    private NodeType type;

    private List<String> dependencies;
    private NodeConfigRequest config;
    private Map<String, String> inputs;
    private Map<String, String> outputs;
    private Map<String, Object> metadata;
}
