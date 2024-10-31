package com.niko.langchain4jworkflow.workflow.core;

import java.util.*;

public class GraphBuilder {
    private final Map<String, Node> nodes = new HashMap<>();
    private final List<String> startNodes = new ArrayList<>();

    public void addNode(Node node) {
        nodes.put(node.getName(), node);
        if (node.getDependencies() == null || node.getDependencies().isEmpty()) {
            startNodes.add(node.getName());
        }
    }

    public WorkflowDefinition build(String name) {
        return WorkflowDefinition.builder()
                .name(name)
                .nodes(new HashMap<>(nodes))
                .startNodes(new ArrayList<>(startNodes))
                .build();
    }
}
