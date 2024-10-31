package com.niko.langchain4jworkflow.workflow.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryWorkflowRegistry implements WorkflowRegistry {
    private final Map<String, WorkflowDefinition> workflows = new ConcurrentHashMap<>();

    @Override
    public void register(WorkflowDefinition workflow) {
        workflows.put(workflow.getName(), workflow);
    }

    @Override
    public void unregister(String workflowName) {

    }

    @Override
    public Optional<WorkflowDefinition> get(String name) {
        return Optional.ofNullable(workflows.get(name));
    }

    @Override
    public List<WorkflowDefinition> list() {
        return List.of();
    }

    @Override
    public boolean exists(String workflowName) {
        return false;
    }
}
