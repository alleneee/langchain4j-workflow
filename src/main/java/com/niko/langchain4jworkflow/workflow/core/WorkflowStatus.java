package com.niko.langchain4jworkflow.workflow.core;

public enum WorkflowStatus {
    CREATED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED,
    TIMEOUT
}
