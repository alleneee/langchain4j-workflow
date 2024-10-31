package com.niko.langchain4jworkflow.workflow.exceptions;

/**
 * 循环依赖异常
 */
public class CircularDependencyException extends WorkflowException {

    private final List<String> dependencyCycle;

    public CircularDependencyException(List<String> dependencyCycle) {
        super("Circular dependency detected: " +
                String.join(" -> ", dependencyCycle));
        this.dependencyCycle = dependencyCycle;
    }

    public List<String> getDependencyCycle() {
        return dependencyCycle;
    }
}

