package com.niko.langchain4jworkflow.workflow.event;

import com.niko.langchain4jworkflow.workflow.core.WorkflowState;
import lombok.Getter;

@Getter
public class WorkflowCompleteEvent extends WorkflowEvent {
    private final WorkflowState state;

    public WorkflowCompleteEvent(
            String workflowName,
            String executionId,
            WorkflowState state) {
        super(workflowName, executionId);
        this.state = state;
    }
}