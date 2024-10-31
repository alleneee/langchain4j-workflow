package com.niko.langchain4jworkflow.workflow.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ExecutionMetrics {
    private int totalNodes;
    private int completedNodes;
    private int failedNodes;
    private int totalRetries;
    private long totalDuration;
    private Map<String, Object> customMetrics;
}
