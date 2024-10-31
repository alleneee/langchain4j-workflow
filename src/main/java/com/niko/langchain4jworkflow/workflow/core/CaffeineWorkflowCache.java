package com.niko.langchain4jworkflow.workflow.core;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.niko.langchain4jworkflow.workflow.config.CacheConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
public class CaffeineWorkflowCache implements WorkflowCache {

    private final Cache<String, Object> cache;

    public CaffeineWorkflowCache(CacheConfig config) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .maximumSize(config.getMaxSize())
                .expireAfterWrite(config.getTtl());

        if (config.isSoftValues()) {
            builder.softValues();
        }

        if (config.isRecordStats()) {
            builder.recordStats();
        }

        this.cache = builder.build();
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Optional<Object> get(String key) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    @Override
    public void invalidate(String key) {
        cache.invalidate(key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }
}
