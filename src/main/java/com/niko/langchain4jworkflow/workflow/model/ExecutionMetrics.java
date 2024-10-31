package com.niko.langchain4jworkflow.workflow.model;

import lombok.Builder;
import lombok.Data;
import java.time.Duration;

/**
 * 执行指标数据传输对象
 * 用于记录和传递工作流执行的统计指标
 */
@Data
@Builder
public class ExecutionMetrics {
    /**
     * 总节点数
     */
    private int totalNodes;
    
    /**
     * 已完成节点数
     */
    private int completedNodes;
    
    /**
     * 失败节点数
     */
    private int failedNodes;
    
    /**
     * 总重试次数
     */
    private int totalRetries;
    
    /**
     * 执行持续时间
     */
    private Duration duration;

    /**
     * 获取总执行时间（毫秒）
     * @return 执行时间毫秒数
     */
    public long getTotalDurationMillis() {
        return duration != null ? duration.toMillis() : 0L;
    }
}
