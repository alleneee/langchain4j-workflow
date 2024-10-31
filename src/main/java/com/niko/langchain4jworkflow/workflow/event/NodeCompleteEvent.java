package com.niko.langchain4jworkflow.workflow.event;

import com.niko.langchain4jworkflow.workflow.core.WorkflowState;
import lombok.Getter;

@Getter
public class NodeCompleteEvent extends WorkflowEvent {
    private final String nodeName;
    private final WorkflowState state;

    public NodeCompleteEvent(Object source, String workflowName, String executionId, String nodeName, WorkflowState state) {
        super(source, workflowName, executionId);
        this.nodeName = nodeName;
        this.state = state;
    }
}