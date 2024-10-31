package com.niko.langchain4jworkflow.workflow.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private long timestamp;
    private Object details;
}
