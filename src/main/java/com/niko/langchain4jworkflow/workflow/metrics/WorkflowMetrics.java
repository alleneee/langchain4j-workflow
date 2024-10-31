package com.niko.langchain4jworkflow.workflow.metrics;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.Map;

@Data
@Builder
public class WorkflowMetrics {
    private String workflowId;
    private String workflowName;
    private Duration duration;
    private int nodeCount;
    private int successfulNodes;
    private int failedNodes;
    private int retryCount;
    private Map<String, NodeMetrics> nodeMetrics;

    @Data
    @Builder
    public static class NodeMetrics {
        private String nodeName;
        private Duration duration;
        private int attempts;
        private boolean successful;
        private String errorType;
        private Map<String, Object> customMetrics;
    }
}