package com.niko.langchain4jworkflow.workflow.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class WorkflowResponse {
    private String workflowId;
    private String executionId;
    private String status;
    private Map<String, Object> outputs;
    private Map<String, NodeExecutionResponse> nodeExecutions;
    private ExecutionMetrics metrics;
    private Instant startTime;
    private Instant endTime;
    private Long duration;
}
