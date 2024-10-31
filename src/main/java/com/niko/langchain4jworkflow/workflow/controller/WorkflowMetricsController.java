package com.niko.langchain4jworkflow.workflow.controller;

import com.niko.langchain4jworkflow.workflow.metrics.MetricsRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/workflows/metrics")
@RequiredArgsConstructor
@Tag(name = "Workflow Metrics", description = "APIs for workflow metrics")
public class WorkflowMetricsController {

    private final MetricsRegistry metricsRegistry;

    @GetMapping
    @Operation(summary = "Get workflow metrics")
    public ResponseEntity<Map<String, Double>> getMetrics() {
        return ResponseEntity.ok(metricsRegistry.getMetrics());
    }
}