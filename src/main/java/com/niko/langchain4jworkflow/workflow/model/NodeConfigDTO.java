package com.niko.langchain4jworkflow.workflow.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;

@Data
@Builder
public class NodeConfigDTO {
    private Duration timeout;
    private RetryConfigDTO retryConfig;
    private String systemPrompt;
    private Integer maxTokens;
    private Double temperature;
    private boolean async;
    private Map<String, Object> properties;
}