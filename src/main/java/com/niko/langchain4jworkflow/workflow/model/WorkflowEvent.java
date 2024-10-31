package com.niko.langchain4jworkflow.workflow.model;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public abstract class WorkflowEvent extends ApplicationEvent {
    private final String workflowId;
    private final long timestamp;

    protected WorkflowEvent(String workflowId) {
        super(workflowId);
        this.workflowId = workflowId;
        this.timestamp = System.currentTimeMillis();
    }
}