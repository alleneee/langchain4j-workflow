package com.niko.langchain4jworkflow.workflow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties(prefix = "workflow")
public class WorkflowProperties {
    private boolean enabled = true;
    private AI ai = new AI();
    private Async async = new Async();
    private Cache cache = new Cache();
    private Monitor monitor = new Monitor();

    @Data
    public static class AI {
        private String openAiApiKey;
        private String modelName = "gpt-3.5-turbo";
        private Integer maxTokens = 2000;
        private Double temperature = 0.7;
        private Duration timeout = Duration.ofSeconds(30);
        private String baseUrl ;
    }

    @Data
    public static class Async {
        private int corePoolSize = 5;
        private int maxPoolSize = 10;
        private int queueCapacity = 25;
        private String threadNamePrefix = "workflow-async-";
        private Duration keepAliveTime = Duration.ofMinutes(1);
    }

    @Data
    public static class Cache {
        private boolean enabled = true;
        private Duration defaultTtl = Duration.ofHours(1);
        private long maxSize = 10000L;
        private boolean softValues = false;
        private boolean recordStats = true;
    }

    @Data
    public static class Monitor {
        private boolean enabled = true;
        private String metricPrefix = "workflow";
        private boolean detailedMetrics = true;
    }
}
