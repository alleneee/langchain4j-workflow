package com.niko.langchain4jworkflow.workflow.core;

import java.util.List;
import java.util.Optional;

public interface WorkflowRegistry {
    void register(WorkflowDefinition workflow);

    void unregister(String workflowName);

    Optional<WorkflowDefinition> get(String workflowName);

    List<WorkflowDefinition> list();

    boolean exists(String workflowName);
}
