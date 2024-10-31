package com.niko.langchain4jworkflow.workflow.core;

import java.util.ArrayList;
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
    public Optional<WorkflowDefinition> get(String name) {
        return Optional.ofNullable(workflows.get(name));
    }

    @Override
    public void unregister(String name) {
        workflows.remove(name);
    }

    @Override
    public boolean exists(String name) {
        return workflows.containsKey(name);
    }

    @Override
    public List<WorkflowDefinition> getAll() {
        return new ArrayList<>(workflows.values());
    }

    @Override
    public List<WorkflowDefinition> list() {
        return getAll();
    }
}
