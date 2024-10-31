package com.niko.langchain4jworkflow.workflow.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * 节点执行响应对象
 * 用于返回节点执行的结果和状态信息
 */
@Data
@Builder
public class NodeExecutionResponse {
    /**
     * 节点名称
     */
    private String nodeName;
    
    /**
     * 执行状态
     */
    private String status;
    
    /**
     * 输出结果
     */
    private Map<String, Object> outputs;
    
    /**
     * 执行次数
     */
    private int attempts;
    
    /**
     * 错误信息
     */
    private String error;
    
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
