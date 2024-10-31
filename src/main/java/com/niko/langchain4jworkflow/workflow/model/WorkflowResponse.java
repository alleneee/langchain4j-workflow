package com.niko.langchain4jworkflow.workflow.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * 工作流执行响应对象
 * 用于返回工作流执行的结果和状态信息
 */
@Data
@Builder
public class WorkflowResponse {
    /**
     * 工作流ID
     */
    private String workflowId;
    
    /**
     * 执行ID
     */
    private String executionId;
    
    /**
     * 执行状态
     */
    private String status;
    
    /**
     * 输出结果
     */
    private Map<String, Object> outputs;
    
    /**
     * 节点执行信息
     */
    private Map<String, NodeExecutionResponse> nodeExecutions;
    
    /**
     * 执行指标
     */
    private ExecutionMetrics metrics;
    
    /**
     * 开始时间
     */
    private Instant startTime;
    
    /**
     * 结束时间
     */
    private Instant endTime;
    
    /**
     * 执行持续时间
     */
    private Duration duration;
}
