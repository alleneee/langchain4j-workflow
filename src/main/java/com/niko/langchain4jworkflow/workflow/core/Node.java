package com.niko.langchain4jworkflow.workflow.core;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class Node {
    private final String name;
    private final String description;
    private final NodeType type;
    private final Method method;
    private final Object target;
    private final List<String> dependencies;
    private final NodeConfig config;
    private final Map<String, Class<?>> inputs;
    private final Map<String, Class<?>> outputs;

    @Data
    @Builder
    public static class NodeConfig {
        private Duration timeout;
        private RetryConfig retryConfig;
        private String systemPrompt;
        private Integer maxTokens;
        private Double temperature;
        private boolean async;
    }

    @Data
    @Builder
    public static class RetryConfig {
        private int maxAttempts;
        private Duration initialDelay;
        private double multiplier;
        private List<Class<? extends Exception>> retryableExceptions;
    }

    public boolean canExecute(WorkflowState state) {
        if (dependencies == null || dependencies.isEmpty()) {
            return true;
        }
        return dependencies.stream()
                .allMatch(state::isNodeCompleted);
    }
}

