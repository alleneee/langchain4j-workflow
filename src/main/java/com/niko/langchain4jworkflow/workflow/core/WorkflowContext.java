package com.niko.langchain4jworkflow.workflow.core;

import lombok.Builder;
import lombok.Data;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@Data
@Builder
public class WorkflowContext {
    private final String workflowId;
    private final String workflowName;
    private final ApplicationContext applicationContext;
    private final Map<String, Object> properties;
    private final WorkflowState state;
}
