package com.niko.langchain4jworkflow.workflow.metrics;

import com.niko.langchain4jworkflow.workflow.model.MetricsResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MicrometerMetricsRegistry implements MetricsRegistry {
    private final MeterRegistry registry;
    private static final String PREFIX = "workflow.";

    @Override
    public void incrementCounter(String name, String... tags) {
        Counter counter = Counter.builder(PREFIX + name)
                .tags(tags)
                .register(registry);
        counter.increment();
    }

    @Override
    public void recordDuration(String name, Duration duration, String... tags) {
        Timer timer = Timer.builder(PREFIX + name)
                .tags(tags)
                .register(registry);
        timer.record(duration.toNanos(), TimeUnit.NANOSECONDS);
    }

    @Override
    public void recordValue(String name, double value, String... tags) {
        registry.gauge(PREFIX + name, value);
    }

    @Override
    public void recordWorkflowExecution(String workflowName, Duration duration, boolean success) {
        String status = success ? "success" : "failure";
        Timer timer = Timer.builder(PREFIX + "execution")
                .tag("workflow", workflowName)
                .tag("status", status)
                .register(registry);
        timer.record(duration.toNanos(), TimeUnit.NANOSECONDS);
    }

    @Override
    public void recordTime(String name, long timeInMillis, String... tags) {
        Timer timer = Timer.builder(PREFIX + name)
                .tags(tags)
                .register(registry);
        timer.record(timeInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public MetricsResponse collectMetrics() {
        Map<String, Long> counters = new HashMap<>();
        Map<String, Double> gauges = new HashMap<>();
        Map<String, MetricsResponse.TimerMetrics> timers = new HashMap<>();

        registry.forEachMeter(meter -> {
            String name = meter.getId().getName();
            if (!name.startsWith(PREFIX)) {
                return;
            }
            
            if (meter instanceof Counter) {
                counters.put(name, (long) ((Counter) meter).count());
            } else if (meter instanceof Timer) {
                Timer timer = (Timer) meter;
                timers.put(name, MetricsResponse.TimerMetrics.builder()
                        .count(timer.count())
                        .mean(timer.mean(TimeUnit.MILLISECONDS))
                        .max(timer.max(TimeUnit.MILLISECONDS))
                        .min(timer.totalTime(TimeUnit.MILLISECONDS) / timer.count())
                        .p95(timer.percentile(0.95, TimeUnit.MILLISECONDS))
                        .p99(timer.percentile(0.99, TimeUnit.MILLISECONDS))
                        .build());
            } else if (meter instanceof Gauge) {
                gauges.put(name, ((Gauge) meter).value());
            }
        });

        return MetricsResponse.builder()
                .counters(counters)
                .gauges(gauges)
                .timers(timers)
                .build();
    }
}