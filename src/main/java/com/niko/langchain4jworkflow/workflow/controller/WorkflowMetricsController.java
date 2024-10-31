package com.niko.langchain4jworkflow.workflow.controller;

import com.niko.langchain4jworkflow.workflow.metrics.MetricsRegistry;
import com.niko.langchain4jworkflow.workflow.model.MetricsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workflows/metrics")
@RequiredArgsConstructor
public class WorkflowMetricsController {
    private final MetricsRegistry metricsRegistry;

    @GetMapping
    public MetricsResponse getMetrics() {
        return metricsRegistry.collectMetrics();
    }
}