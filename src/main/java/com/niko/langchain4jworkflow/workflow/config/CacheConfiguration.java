package com.niko.langchain4jworkflow.workflow.config;

import com.niko.langchain4jworkflow.workflow.core.CaffeineWorkflowCache;
import com.niko.langchain4jworkflow.workflow.core.WorkflowCache;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "workflow.cache")
    public CacheConfig cacheConfig() {
        return CacheConfig.builder()
                .maxSize(10000L)
                .defaultTtl(Duration.ofMinutes(10))
                .softValues(true)
                .recordStats(true)
                .build();
    }

    @Bean
    public WorkflowCache workflowCache(CacheConfig config) {
        return new CaffeineWorkflowCache(config);
    }
}
