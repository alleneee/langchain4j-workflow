package com.niko.langchain4jworkflow.workflow.core;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 工作流节点定义
 * 表示工作流中的一个执行单元，包含节点的所有配置和执行信息
 */
@Getter
@Builder
public class Node {
    /**
     * 节点名称
     */
    private final String name;
    
    /**
     * 节点描述
     */
    private final String description;
    
    /**
     * 节点类型
     */
    private final NodeType type;
    
    /**
     * 节点依赖列表
     */
    private final List<String> dependencies;
    
    /**
     * 节点配置
     */
    private final NodeConfig config;
    
    /**
     * 节点输入参数类型映射
     */
    private final Map<String, Class<?>> inputs;
    
    /**
     * 节点输出参数类型映射
     */
    private final Map<String, Class<?>> outputs;
    
    /**
     * 节点元数据
     */
    private final Map<String, Object> metadata;
    
    /**
     * 节点执行方法
     */
    private final Method method;
    
    /**
     * 节点执行目标对象
     */
    private final Object target;

    /**
     * 节点配置信息
     */
    @Data
    @Builder
    public static class NodeConfig {
        /**
         * 执行超时时间
         */
        private Duration timeout;
        
        /**
         * 重试配置
         */
        private RetryConfig retryConfig;
        
        /**
         * 是否异步执行
         */
        private boolean async;
        
        /**
         * AI系统提示词
         */
        private String systemPrompt;
        
        /**
         * 其他配置属性
         */
        private Map<String, Object> properties;
        
        // AI节点特有配置
        /**
         * 最大token数量
         */
        private Integer maxTokens;
        
        /**
         * 温度参数
         */
        private Double temperature;
        
        /**
         * 模型名称
         */
        private String model;
        
        // 并行节点特有配置
        /**
         * 最大并发数
         */
        private Integer maxConcurrency;
        
        /**
         * 是否等待所有任务完成
         */
        private boolean waitForAll;
        
        // 函数节点特有配置
        /**
         * Bean名称
         */
        private String beanName;
        
        /**
         * 方法名称
         */
        private String methodName;
    }

    /**
     * 重试配置信息
     */
    @Data
    @Builder
    public static class RetryConfig {
        /**
         * 最大重试次数
         */
        private int maxAttempts;
        
        /**
         * 重试延迟时间
         */
        private Duration delay;
        
        /**
         * 重试延迟时间的乘数因子
         */
        private double multiplier;
        
        /**
         * 可重试的异常类型列表
         */
        private List<Class<? extends Throwable>> retryableExceptions;
    }

    /**
     * 检查节点是否可以执行
     * @param state 工作流状态
     * @return 是否可以执行
     */
    public boolean canExecute(WorkflowState state) {
        if (dependencies == null || dependencies.isEmpty()) {
            return true;
        }
        return dependencies.stream()
                .allMatch(state::isNodeCompleted);
    }
}

