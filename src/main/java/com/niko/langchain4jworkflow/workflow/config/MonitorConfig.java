package com.niko.langchain4jworkflow.workflow.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonitorConfig {
    private boolean enabled;
    private String metricPrefix;
    private boolean detailedMetrics;
}
