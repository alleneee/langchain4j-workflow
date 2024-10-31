package com.niko.langchain4jworkflow.workflow.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public abstract class WorkflowEvent extends ApplicationEvent {
    private final String workflowName;
    private final String executionId;
    private final long timestamp;

    protected WorkflowEvent(String workflowName, String executionId) {
        super(workflowName);
        this.workflowName = workflowName;
        this.executionId = executionId;
        this.timestamp = System.currentTimeMillis();
    }
}
