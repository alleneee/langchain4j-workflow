package com.niko.langchain4jworkflow.workflow.config;


import com.niko.langchain4jworkflow.workflow.aspect.WorkflowAspect;
import com.niko.langchain4jworkflow.workflow.core.*;
import com.niko.langchain4jworkflow.workflow.metrics.MetricsRegistry;
import com.niko.langchain4jworkflow.workflow.metrics.MicrometerMetricsRegistry;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
@EnableConfigurationProperties(WorkflowProperties.class)
@ConditionalOnProperty(value = "workflow.enabled", havingValue = "true", matchIfMissing = true)
public class WorkflowAutoConfiguration {

    private final WorkflowProperties properties;
    private final ApplicationContext applicationContext;

    @Bean
    @ConditionalOnMissingBean
    public WorkflowEngine workflowEngine(
            WorkflowRegistry registry,
            ThreadPoolTaskExecutor asyncExecutor,
            WorkflowCache cache,
            MetricsRegistry metricsRegistry,
            ChatLanguageModel chatModel) {

        return AnnotationBasedWorkflowEngine.builder()
                .registry(registry)
                .asyncExecutor(asyncExecutor)
                .cache(cache)
                .metrics(metricsRegistry)
                .chatModel(chatModel)
                .properties(properties)
                .applicationContext(applicationContext)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public WorkflowRegistry workflowRegistry() {
        return new InMemoryWorkflowRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getAsync().getCorePoolSize());
        executor.setMaxPoolSize(properties.getAsync().getMaxPoolSize());
        executor.setQueueCapacity(properties.getAsync().getQueueCapacity());
        executor.setThreadNamePrefix(properties.getAsync().getThreadNamePrefix());
        executor.setKeepAliveSeconds((int) properties.getAsync().getKeepAliveTime().toSeconds());
        executor.initialize();
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "workflow.cache.enabled", havingValue = "true", matchIfMissing = true)
    public WorkflowCache workflowCache() {
        return new CaffeineWorkflowCache(CacheConfig.builder()
                .maxSize(properties.getCache().getMaxSize())
                .ttl(properties.getCache().getDefaultTtl())  // 修改这里，使用ttl而不是defaultTtl
                .softValues(properties.getCache().isSoftValues())
                .recordStats(properties.getCache().isRecordStats())
                .build());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "workflow.monitor.enabled", havingValue = "true", matchIfMissing = true)
    public MetricsRegistry metricsRegistry(MeterRegistry meterRegistry) {
        return new MicrometerMetricsRegistry(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(properties.getAi().getOpenAiApiKey())
                .modelName(properties.getAi().getModelName())
                .maxTokens(properties.getAi().getMaxTokens())
                .temperature(properties.getAi().getTemperature())
                .timeout(properties.getAi().getTimeout())
                .build();
    }

    @Bean
    public WorkflowAspect workflowAspect(
            WorkflowEngine engine,
            MetricsRegistry metricsRegistry) {
        return new WorkflowAspect(engine, metricsRegistry);
    }
}
