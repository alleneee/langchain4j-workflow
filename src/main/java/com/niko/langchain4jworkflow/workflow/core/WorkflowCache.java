package com.niko.langchain4jworkflow.workflow.core;

import java.util.Optional;

/**
 * 工作流缓存接口
 */
public interface WorkflowCache {
    /**
     * 将值存入缓存
     */
    void put(String key, Object value);

    /**
     * 从缓存中获取值
     */
    Optional<Object> get(String key);

    /**
     * 从缓存中移除值
     */
    void invalidate(String key);

    /**
     * 清空缓存
     */
    void clear();
}
