package com.niko.langchain4jworkflow.workflow.model;

import com.niko.langchain4jworkflow.workflow.core.WorkflowStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class WorkflowDTO {
    private String id;
    private String name;
    private String description;
    private String version;
    private WorkflowStatus status;
    private Set<String> labels;
    private List<NodeDTO> nodes;
    private WorkflowConfigDTO config;
    private Map<String, Object> metadata;
    private Instant createdAt;
    private Instant updatedAt;
}
