package com.niko.langchain4jworkflow.workflow.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retry {
    int maxAttempts() default 3;

    long initialDelay() default 1000;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    double multiplier() default 2.0;

    Class<? extends Exception>[] retryFor() default {Exception.class};

    Class<? extends Exception>[] noRetryFor() default {};
}
