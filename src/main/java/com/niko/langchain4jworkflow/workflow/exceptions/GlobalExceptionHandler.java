package com.niko.langchain4jworkflow.workflow.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WorkflowNotFoundException.class)
    public ResponseEntity<?> handleWorkflowNotFound(WorkflowNotFoundException e) {
        log.error("Workflow not found: {}", e.getWorkflowName());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(NodeExecutionException.class)
    public ResponseEntity<?> handleNodeExecution(NodeExecutionException e) {
        log.error("Node execution failed: {}", e.getNodeName(), e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidation(ValidationException e) {
        log.error("Validation failed: {}", e.getValidationErrors());
        return ResponseEntity.badRequest()
                .body(new ValidationErrorResponse(e.getValidationErrors()));
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<?> handleTimeout(TimeoutException e) {
        log.error("Operation timed out after {} ms", e.getTimeoutMillis());
        return ResponseEntity.status(504)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(RetryExhaustedException.class)
    public ResponseEntity<?> handleRetryExhausted(RetryExhaustedException e) {
        log.error("Retry exhausted for {}: {} attempts",
                e.getOperation(), e.getAttempts());
        return ResponseEntity.status(503)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception e) {
        log.error("Unexpected error", e);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("Internal server error"));
    }
}