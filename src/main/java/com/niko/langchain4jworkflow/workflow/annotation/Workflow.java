package com.niko.langchain4jworkflow.workflow.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Workflow {
    @AliasFor("name")
    String value() default "";

    String name() default "";

    String description() default "";

    String version() default "1.0";

    boolean enabled() default true;
}
