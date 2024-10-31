package com.niko.langchain4jworkflow.workflow.core;

import java.util.List;
import java.util.Optional;

public interface WorkflowRegistry {
    void register(WorkflowDefinition workflow);

    Optional<WorkflowDefinition> get(String name);

    void unregister(String name);

    boolean exists(String name);

    List<WorkflowDefinition> getAll();

    List<WorkflowDefinition> list();
}
