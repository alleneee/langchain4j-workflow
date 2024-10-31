package com.niko.langchain4jworkflow.workflow.model;

import com.niko.langchain4jworkflow.workflow.core.NodeType;
import com.niko.langchain4jworkflow.workflow.exceptions.ValidationException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class WorkflowValidator {

    public void validateCreateRequest(CreateWorkflowRequest request) {
        Map<String, List<String>> errors = new HashMap<>();

        // 验证工作流名称
        if (!isValidName(request.getName())) {
            errors.computeIfAbsent("name", k -> new ArrayList<>())
                    .add("Invalid workflow name format");
        }

        // 验证节点
        validateNodes(request.getNodes(), errors);

        // 验证节点依赖
        validateDependencies(request.getNodes(), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private boolean isValidName(String name) {
        return name != null &&
                name.matches("^[a-zA-Z0-9_-]{1,64}$");
    }

    private void validateNodes(
            List<CreateNodeRequest> nodes,
            Map<String, List<String>> errors) {
        // 检查节点名称唯一性
        Set<String> nodeNames = new HashSet<>();
        for (CreateNodeRequest node : nodes) {
            if (!nodeNames.add(node.getName())) {
                errors.computeIfAbsent("nodes", k -> new ArrayList<>())
                        .add("Duplicate node name: " + node.getName());
            }
        }

        // 验证每个节点的配置
        for (CreateNodeRequest node : nodes) {
            validateNodeConfig(node, errors);
        }
    }

    private void validateNodeConfig(
            CreateNodeRequest node,
            Map<String, List<String>> errors) {
        String prefix = "node." + node.getName();

        // 验证AI节点的必要配置
        if (node.getType() == NodeType.AI) {
            if (node.getConfig() == null ||
                    node.getConfig().getSystemPrompt() == null) {
                errors.computeIfAbsent(prefix, k -> new ArrayList<>())
                        .add("AI node requires system prompt");
            }
        }

        // 验证输入输出
        if (node.getInputs() != null) {
            validateIOConfig(prefix + ".inputs",
                    node.getInputs(), errors);
        }
        if (node.getOutputs() != null) {
            validateIOConfig(prefix + ".outputs",
                    node.getOutputs(), errors);
        }
    }

    private void validateIOConfig(
            String prefix,
            Map<String, String> config,
            Map<String, List<String>> errors) {
        for (Map.Entry<String, String> entry : config.entrySet()) {
            if (!isValidName(entry.getKey())) {
                errors.computeIfAbsent(prefix, k -> new ArrayList<>())
                        .add("Invalid parameter name: " + entry.getKey());
            }
        }
    }

    private void validateDependencies(
            List<CreateNodeRequest> nodes,
            Map<String, List<String>> errors) {
        // 构建节点名称集合
        Set<String> nodeNames = nodes.stream()
                .map(CreateNodeRequest::getName)
                .collect(Collectors.toSet());

        // 检查依赖的有效性
        for (CreateNodeRequest node : nodes) {
            if (node.getDependencies() != null) {
                for (String dep : node.getDependencies()) {
                    if (!nodeNames.contains(dep)) {
                        errors.computeIfAbsent(
                                "node." + node.getName() + ".dependencies",
                                k -> new ArrayList<>()
                        ).add("Invalid dependency: " + dep);
                    }
                }
            }
        }

        // 检查循环依赖
        if (hasCyclicDependencies(nodes)) {
            errors.computeIfAbsent("dependencies", k -> new ArrayList<>())
                    .add("Cyclic dependencies detected");
        }
    }

    private boolean hasCyclicDependencies(List<CreateNodeRequest> nodes) {
        // 构建依赖图
        Map<String, Set<String>> graph = new HashMap<>();
        for (CreateNodeRequest node : nodes) {
            graph.put(node.getName(), new HashSet<>());
            if (node.getDependencies() != null) {
                graph.get(node.getName()).addAll(node.getDependencies());
            }
        }

        // DFS检测循环
        Set<String> visited = new HashSet<>();
        Set<String> recStack = new HashSet<>();

        for (String node : graph.keySet()) {
            if (hasCycle(node, graph, visited, recStack)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasCycle(
            String node,
            Map<String, Set<String>> graph,
            Set<String> visited,
            Set<String> recStack) {
        if (recStack.contains(node)) {
            return true;
        }
        if (visited.contains(node)) {
            return false;
        }

        visited.add(node);
        recStack.add(node);

        for (String dep : graph.get(node)) {
            if (hasCycle(dep, graph, visited, recStack)) {
                return true;
            }
        }

        recStack.remove(node);
        return false;
    }
}