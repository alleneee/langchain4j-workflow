package com.niko.langchain4jworkflow.workflow.controller;

import com.niko.langchain4jworkflow.workflow.model.*;
import com.niko.langchain4jworkflow.workflow.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
@Tag(name = "Workflow Management", description = "APIs for managing workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    @Operation(summary = "Create a new workflow")
    public ResponseEntity<WorkflowDTO> createWorkflow(
            @Validated @RequestBody CreateWorkflowRequest request) {
        log.info("Creating workflow: {}", request.getName());
        WorkflowDTO workflow = workflowService.createWorkflow(request);
        return ResponseEntity.ok(workflow);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing workflow")
    public ResponseEntity<WorkflowDTO> updateWorkflow(
            @PathVariable String id,
            @Validated @RequestBody UpdateWorkflowRequest request) {
        log.info("Updating workflow: {}", id);
        WorkflowDTO workflow = workflowService.updateWorkflow(id, request);
        return ResponseEntity.ok(workflow);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a workflow")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable String id) {
        log.info("Deleting workflow: {}", id);
        workflowService.deleteWorkflow(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workflow details")
    public ResponseEntity<WorkflowDTO> getWorkflow(@PathVariable String id) {
        log.info("Getting workflow: {}", id);
        return workflowService.getWorkflow(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "List all workflows")
    public ResponseEntity<List<WorkflowDTO>> listWorkflows() {
        log.info("Listing all workflows");
        List<WorkflowDTO> workflows = workflowService.listWorkflows();
        return ResponseEntity.ok(workflows);
    }

    @PostMapping("/{id}/execute")
    @Operation(summary = "Execute a workflow")
    public ResponseEntity<WorkflowResponse> executeWorkflow(
            @PathVariable String id,
            @Validated @RequestBody ExecuteWorkflowRequest request) {
        log.info("Executing workflow: {}", id);
        request.setWorkflowName(id);
        WorkflowResponse response = workflowService.executeWorkflow(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/executions/{executionId}/stop")
    @Operation(summary = "Stop a workflow execution")
    public ResponseEntity<Void> stopWorkflow(
            @PathVariable String executionId) {
        log.info("Stopping workflow execution: {}", executionId);
        workflowService.stopWorkflow(executionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/executions/{executionId}")
    @Operation(summary = "Get workflow execution status")
    public ResponseEntity<WorkflowResponse> getExecutionStatus(
            @PathVariable String executionId) {
        log.info("Getting execution status: {}", executionId);
        WorkflowResponse status = workflowService.getExecutionStatus(executionId);
        return ResponseEntity.ok(status);
    }
}