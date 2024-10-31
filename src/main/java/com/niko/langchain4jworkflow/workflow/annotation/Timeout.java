package com.niko.langchain4jworkflow.workflow.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Timeout {
    long value();
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
