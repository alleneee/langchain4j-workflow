package com.niko.langchain4jworkflow.workflow.core;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WorkflowContextHolder {
    private static final ThreadLocal<WorkflowContext> contextHolder =
            new ThreadLocal<>();

    public static void setContext(WorkflowContext context) {
        contextHolder.set(context);
    }

    public static WorkflowContext getContext() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}
