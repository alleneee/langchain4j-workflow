package com.niko.langchain4jworkflow.workflow.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class MicrometerMetricsRegistry implements MetricsRegistry {
    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final Map<String, Timer> timers = new ConcurrentHashMap<>();
    private final Map<String, Gauge> gauges = new ConcurrentHashMap<>();

    @Override
    public void incrementCounter(String name, String... tags) {
        getCounter(name, tags).increment();
    }

    @Override
    public void decrementCounter(String name, String... tags) {
        getCounter(name, tags).increment(-1);
    }

    @Override
    public void recordTime(String name, long timeMillis, String... tags) {
        getTimer(name, tags).record(timeMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void recordValue(String name, double value, String... tags) {
        Gauge gauge = gauges.computeIfAbsent(
                createMetricKey(name, tags),
                key -> Gauge.builder(name, () -> value)
                        .tags(tags)
                        .register(meterRegistry)
        );
        gauge.value();
    }

    @Override
    public void recordHistogram(String name, double value, String... tags) {
        DistributionSummary.builder(name)
                .tags(tags)
                .register(meterRegistry)
                .record(value);
    }

    @Override
    public Map<String, Double> getMetrics() {
        Map<String, Double> metrics = new HashMap<>();

        // 收集计数器指标
        counters.forEach((key, counter) ->
                metrics.put(key, counter.count()));

        // 收集定时器指标
        timers.forEach((key, timer) -> {
            metrics.put(key + ".count", (double) timer.count());
            metrics.put(key + ".mean", timer.mean(TimeUnit.MILLISECONDS));
            metrics.put(key + ".max", timer.max(TimeUnit.MILLISECONDS));
        });

        // 收集仪表盘指标
        gauges.forEach((key, gauge) ->
                metrics.put(key, gauge.value()));

        return metrics;
    }

    @Override
    public void recordWorkflowExecution(String workflowName, Duration duration, boolean success) {

    }

    private Counter getCounter(String name, String... tags) {
        return counters.computeIfAbsent(
                createMetricKey(name, tags),
                key -> Counter.builder(name)
                        .tags(tags)
                        .register(meterRegistry)
        );
    }

    private Timer getTimer(String name, String... tags) {
        return timers.computeIfAbsent(
                createMetricKey(name, tags),
                key -> Timer.builder(name)
                        .tags(tags)
                        .register(meterRegistry)
        );
    }

    private String createMetricKey(String name, String... tags) {
        StringBuilder key = new StringBuilder(name);
        for (int i = 0; i < tags.length; i += 2) {
            key.append('.').append(tags[i])
                    .append('.').append(tags[i + 1]);
        }
        return key.toString();
    }
}