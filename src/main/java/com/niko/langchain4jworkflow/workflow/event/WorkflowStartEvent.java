package com.niko.langchain4jworkflow.workflow.event;


import lombok.Getter;

import java.util.Map;

@Getter
public class WorkflowStartEvent extends WorkflowEvent {
    private final Map<String, Object> inputs;

    public WorkflowStartEvent(
            String workflowName,
            String executionId,
            Map<String, Object> inputs) {
        super(workflowName, executionId);
        this.inputs = inputs;
    }
}

