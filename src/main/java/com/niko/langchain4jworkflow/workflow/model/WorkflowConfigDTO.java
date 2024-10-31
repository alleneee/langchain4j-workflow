package com.niko.langchain4jworkflow.workflow.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.Map;

/**
 * 工作流配置数据传输对象
 * 用于在系统各层之间传递工作流配置信息
 */
@Data
@Builder
public class WorkflowConfigDTO {
    /**
     * 执行超时时间
     */
    private Duration timeout;
    
    /**
     * 重试配置
     */
    private RetryConfigDTO retryConfig;
    
    /**
     * 缓存配置
     */
    private CacheConfigDTO cacheConfig;
    
    /**
     * 监控配置
     */
    private MonitorConfigDTO monitorConfig;
    
    /**
     * 是否启用异步执行
     */
    private boolean asyncEnabled;
    
    /**
     * 其他配置属性
     */
    private Map<String, Object> properties;
}
