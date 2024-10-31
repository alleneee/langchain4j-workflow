package com.niko.langchain4jworkflow.workflow.controller;

import com.niko.langchain4jworkflow.workflow.model.*;
import com.niko.langchain4jworkflow.workflow.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    private final WorkflowService workflowService;

    @PostMapping
    public ResponseEntity<WorkflowDTO> createWorkflow(
            @Valid @RequestBody CreateWorkflowRequest request) {
        WorkflowDTO workflow = workflowService.createWorkflow(request);
        return ResponseEntity.ok(workflow);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowDTO> updateWorkflow(
            @PathVariable String id,
            @Valid @RequestBody UpdateWorkflowRequest request) {
        WorkflowDTO workflow = workflowService.updateWorkflow(id, request);
        return ResponseEntity.ok(workflow);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable String id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDTO> getWorkflow(@PathVariable String id) {
        WorkflowDTO workflow = workflowService.getWorkflow(id);
        return ResponseEntity.ok(workflow);
    }

    @GetMapping
    public ResponseEntity<List<WorkflowDTO>> listWorkflows() {
        List<WorkflowDTO> workflows = workflowService.listWorkflows();
        return ResponseEntity.ok(workflows);
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<WorkflowResponse> executeWorkflow(
            @PathVariable String id,
            @Valid @RequestBody ExecuteWorkflowRequest request) {
        request.setWorkflowName(id);
        WorkflowResponse response = workflowService.executeWorkflow(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/executions/{executionId}/stop")
    public ResponseEntity<Void> stopWorkflow(
            @PathVariable String executionId) {
        workflowService.stopWorkflow(executionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/executions/{executionId}")
    public ResponseEntity<WorkflowResponse> getExecutionStatus(
            @PathVariable String executionId) {
        WorkflowResponse response = workflowService.getExecutionStatus(executionId);
        return ResponseEntity.ok(response);
    }
}