package com.niko.langchain4jworkflow.workflow.exceptions;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String message;
    private final long timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
}

