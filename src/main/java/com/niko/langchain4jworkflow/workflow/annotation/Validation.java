package com.niko.langchain4jworkflow.workflow.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validation {
    int minLength() default 0;

    int maxLength() default Integer.MAX_VALUE;

    String pattern() default "";

    boolean required() default true;

    String message() default "";
}