package com.niko.langchain4jworkflow.workflow.core;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 工作流定义
 * 描述工作流的完整结构和配置信息
 */
@Getter
@Builder
public class WorkflowDefinition {
    /**
     * 工作流名称
     */
    private final String name;
    
    /**
     * 工作流描述
     */
    private final String description;
    
    /**
     * 工作流节点映射
     */
    private final Map<String, Node> nodes;
    
    /**
     * 起始节点列表
     */
    private final List<String> startNodes;
    
    /**
     * 工作流元数据
     */
    private final Map<String, Object> metadata;
    
    /**
     * 工作流配置
     */
    private WorkflowConfig config;

    /**
     * 设置工作流配置
     * @param config 工作流配置
     */
    public void setConfig(WorkflowConfig config) {
        this.config = config;
    }

    /**
     * 工作流配置信息
     */
    @Data
    @Builder
    public static class WorkflowConfig {
        /**
         * 执行超时时间
         */
        private Duration timeout;
        
        /**
         * 重试配置
         */
        private RetryConfig retryConfig;
        
        /**
         * 缓存配置
         */
        private CacheConfig cacheConfig;
        
        /**
         * 监控配置
         */
        private MonitorConfig monitorConfig;
        
        /**
         * 是否启用异步执行
         */
        private boolean asyncEnabled;

        /**
         * 重试配置信息
         */
        @Data
        @Builder
        public static class RetryConfig {
            private int maxAttempts;
            private Duration delay;
            private double multiplier;
            private List<Class<? extends Throwable>> retryableExceptions;
        }

        /**
         * 缓存配置信息
         */
        @Data
        @Builder
        public static class CacheConfig {
            private boolean enabled;
            private Duration ttl;
            private long maxSize;
            private boolean softValues;
            private boolean recordStats;
        }

        /**
         * 监控配置信息
         */
        @Data
        @Builder
        public static class MonitorConfig {
            private boolean enabled;
            private String metricPrefix;
            private boolean detailedMetrics;
        }
    }
}
