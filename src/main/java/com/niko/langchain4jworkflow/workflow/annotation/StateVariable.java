package com.niko.langchain4jworkflow.workflow.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StateVariable {
    String value();

    boolean required() default true;
}
