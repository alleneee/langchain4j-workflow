package com.niko.langchain4jworkflow.workflow.metrics;

import com.niko.langchain4jworkflow.workflow.model.MetricsResponse;
import java.time.Duration;

public interface MetricsRegistry {
    void incrementCounter(String name, String... tags);
    
    void recordDuration(String name, Duration duration, String... tags);
    
    void recordValue(String name, double value, String... tags);
    
    void recordWorkflowExecution(String workflowName, Duration duration, boolean success);
    
    void recordTime(String name, long timeInMillis, String... tags);
    
    MetricsResponse collectMetrics();
}
