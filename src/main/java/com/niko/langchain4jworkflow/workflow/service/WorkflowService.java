package com.niko.langchain4jworkflow.workflow.service;

import com.niko.langchain4jworkflow.workflow.model.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface WorkflowService {
    WorkflowDTO createWorkflow(CreateWorkflowRequest request);
    
    WorkflowDTO updateWorkflow(String id, UpdateWorkflowRequest request);
    
    void deleteWorkflow(String id);
    
    WorkflowDTO getWorkflow(String id);
    
    List<WorkflowDTO> listWorkflows();
    
    WorkflowResponse executeWorkflow(ExecuteWorkflowRequest request);
    
    void stopWorkflow(String executionId);
    
    WorkflowResponse getExecutionStatus(String executionId);
}