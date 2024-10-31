package com.niko.langchain4jworkflow.workflow.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Monitor {
    String[] metrics() default {};

    boolean logInput() default false;

    boolean logOutput() default false;

    String[] tags() default {};
}
