package com.niko.langchain4jworkflow.workflow.metrics;

import java.time.Duration;
import java.util.Map;

/**
 * 指标注册接口
 */
public interface MetricsRegistry {
    void incrementCounter(String name, String... tags);

    void decrementCounter(String name, String... tags);

    void recordTime(String name, long timeMillis, String... tags);

    void recordValue(String name, double value, String... tags);

    void recordHistogram(String name, double value, String... tags);

    Map<String, Double> getMetrics();

    void recordWorkflowExecution(String workflowName, Duration duration, boolean success);
}
