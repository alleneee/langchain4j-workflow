package com.niko.langchain4jworkflow.workflow.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;

@Data
public class CreateWorkflowRequest {
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Invalid name format")
    private String name;

    private String description;

    @NotEmpty(message = "At least one node is required")
    private List<CreateNodeRequest> nodes;

    private WorkflowConfigRequest config;
    private Map<String, Object> metadata;
}