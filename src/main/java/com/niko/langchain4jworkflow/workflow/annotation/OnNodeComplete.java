package com.niko.langchain4jworkflow.workflow.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnNodeComplete {
    String[] value() default {};
}