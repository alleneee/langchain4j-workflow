package com.niko.langchain4jworkflow.workflow.annotation;

import com.niko.langchain4jworkflow.workflow.core.NodeType;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Node {
    String name() default "";

    NodeType type() default NodeType.FUNCTION;

    String[] dependsOn() default {};

    String systemPrompt() default "";
}
