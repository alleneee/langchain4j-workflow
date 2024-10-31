package com.niko.langchain4jworkflow.workflow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "workflow")
public class WorkflowProperties {
    private boolean enabled = true;
    private String basePackage;
    private Async async = new Async();
    private Cache cache = new Cache();
    private Retry retry = new Retry();
    private Monitor monitor = new Monitor();
    private AI ai = new AI();

    @Data
    public static class Async {
        private boolean enabled = true;
        private int corePoolSize = 5;
        private int maxPoolSize = 10;
        private int queueCapacity = 100;
        private String threadNamePrefix = "workflow-async-";
        private Duration keepAliveTime = Duration.ofMinutes(1);
    }

    @Data
    public static class Cache {
        private boolean enabled = true;
        private Duration defaultTtl = Duration.ofHours(1);
        private Long maxSize = 10000L;
        private boolean softValues = false;
        private boolean recordStats = true;
    }

    @Data
    public static class Retry {
        private boolean enabled = true;
        private int maxAttempts = 3;
        private Duration initialDelay = Duration.ofSeconds(1);
        private double multiplier = 2.0;
        private Duration maxDelay = Duration.ofMinutes(1);
        private List<Class<? extends Exception>> retryableExceptions =
                Arrays.asList(Exception.class);
    }

    @Data
    public static class Monitor {
        private boolean enabled = true;
        private boolean logInput = false;
        private boolean logOutput = false;
        private List<String> defaultMetrics = Arrays.asList(
                "execution_time",
                "success_rate"
        );
        private Duration metricsInterval = Duration.ofSeconds(60);
    }

    @Data
    public static class AI {
        private String openAiApiKey;
        private String modelName = "gpt-3.5-turbo";
        private int maxTokens = 1000;
        private double temperature = 0.7;
        private Duration timeout = Duration.ofSeconds(30);
        private boolean streamResponse = false;
    }
}
