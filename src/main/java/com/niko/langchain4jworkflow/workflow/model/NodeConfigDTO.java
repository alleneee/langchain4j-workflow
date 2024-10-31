package com.niko.langchain4jworkflow.workflow.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.Map;

/**
 * 节点配置数据传输对象
 * 用于在系统各层之间传递节点配置信息
 */
@Data
@Builder
public class NodeConfigDTO {
    /**
     * 执行超时时间
     */
    private Duration timeout;
    
    /**
     * 重试配置
     */
    private RetryConfigDTO retryConfig;
    
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