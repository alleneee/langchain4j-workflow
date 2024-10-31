package com.niko.langchain4jworkflow.workflow.core;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class WorkflowDefinition {
    private String name;
    private String description;
    private String version;
    private Map<String, Node> nodes;
    private List<String> startNodes;
    private Set<String> labels;
    private WorkflowConfig config;

    public void setConfig(WorkflowConfig config) {
        this.config = config;
    }

    @Data
    @Builder
    public static class WorkflowConfig {
        private Duration timeout;
        private RetryConfig retryConfig;
        private CacheConfig cacheConfig;
        private MonitorConfig monitorConfig;
        private boolean asyncEnabled;

        @Data
        @Builder
        public static class RetryConfig {
            private int maxAttempts;
            private Duration delay;
            private double multiplier;
            private List<Class<? extends Throwable>> retryableExceptions;
        }

        @Data
        @Builder
        public static class CacheConfig {
            private boolean enabled;
            private Duration ttl;
            private long maxSize;
            private boolean softValues;
            private boolean recordStats;
        }

        @Data
        @Builder
        public static class MonitorConfig {
            private boolean enabled;
            private String metricPrefix;
            private boolean detailedMetrics;
        }
    }
}
