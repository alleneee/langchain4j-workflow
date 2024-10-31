package com.niko.langchain4jworkflow.workflow.aspect;

import com.niko.langchain4jworkflow.workflow.annotation.Monitor;
import com.niko.langchain4jworkflow.workflow.metrics.MetricsRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MetricsAspect {
    private final MetricsRegistry metricsRegistry;

    @Around("@annotation(monitor)")
    public Object recordMetrics(ProceedingJoinPoint joinPoint, Monitor monitor)
            throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();

        try {
            // 记录调用次数
            metricsRegistry.incrementCounter(
                    methodName + ".calls",
                    "class", joinPoint.getTarget().getClass().getSimpleName()
            );

            // 执行方法
            Object result = joinPoint.proceed();

            // 记录成功次数
            metricsRegistry.incrementCounter(
                    methodName + ".success",
                    "class", joinPoint.getTarget().getClass().getSimpleName()
            );

            return result;

        } catch (Exception e) {
            // 记录失败次数
            metricsRegistry.incrementCounter(
                    methodName + ".failures",
                    "class", joinPoint.getTarget().getClass().getSimpleName(),
                    "error", e.getClass().getSimpleName()
            );
            throw e;

        } finally {
            // 记录执行时间
            long duration = System.currentTimeMillis() - startTime;
            metricsRegistry.recordTime(
                    methodName + ".duration",
                    duration,
                    "class", joinPoint.getTarget().getClass().getSimpleName()
            );

            // 记录自定义指标
            if (monitor.metrics().length > 0) {
                Arrays.stream(monitor.metrics()).forEach(metric ->
                        metricsRegistry.recordValue(
                                methodName + "." + metric,
                                extractMetricValue(metric, joinPoint),
                                "class", joinPoint.getTarget().getClass().getSimpleName()
                        )
                );
            }
        }
    }

    private double extractMetricValue(String metric, ProceedingJoinPoint joinPoint) {
        // 实现从方法参数或返回值中提取指标值的逻辑
        return 0.0;
    }
}