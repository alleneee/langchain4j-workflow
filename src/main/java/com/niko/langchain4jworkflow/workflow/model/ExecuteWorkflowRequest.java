package com.niko.langchain4jworkflow.workflow.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.Map;

@Data
public class ExecuteWorkflowRequest {
    @NotBlank(message = "Workflow name is required")
    private String workflowName;

    @NotNull(message = "Inputs are required")
    private Map<String, Object> inputs;

    private Map<String, Object> context;
    private ExecutionConfigRequest config;
}