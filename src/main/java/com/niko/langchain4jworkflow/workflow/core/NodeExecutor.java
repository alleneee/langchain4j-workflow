package com.niko.langchain4jworkflow.workflow.core;

import java.util.concurrent.CompletableFuture;

public interface NodeExecutor {
    CompletableFuture<WorkflowState> execute(
            Node node,
            WorkflowState state,
            WorkflowContext context
    );
}
