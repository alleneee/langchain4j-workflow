package com.niko.langchain4jworkflow.workflow.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class WorkflowConfigDTO {
    private Duration timeout;
    private RetryConfigDTO retryConfig;
    private CacheConfigDTO cacheConfig;
    private MonitorConfigDTO monitorConfig;
    private boolean asyncEnabled;
    private Map<String, Object> properties;
}
