package com.niko.langchain4jworkflow.workflow.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
    String key() default "";

    String ttl() default "5m";

    boolean enabled() default true;
}
