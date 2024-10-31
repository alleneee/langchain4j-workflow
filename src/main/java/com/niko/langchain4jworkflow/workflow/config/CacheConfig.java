package com.niko.langchain4jworkflow.workflow.config;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;


@Data
@Builder
public class CacheConfig {
    private boolean enabled;
    private Duration ttl;
    private Long maxSize;
    private boolean softValues;
    private boolean recordStats;
    
    // 为了保持与builder方法名一致
    public static class CacheConfigBuilder {
        public CacheConfigBuilder defaultTtl(Duration ttl) {
            this.ttl = ttl;
            return this;
        }
    }
}
