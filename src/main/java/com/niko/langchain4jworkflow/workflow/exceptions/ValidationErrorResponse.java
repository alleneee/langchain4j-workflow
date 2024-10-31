package com.niko.langchain4jworkflow.workflow.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {
    private final Map<String, List<String>> errors;

    public ValidationErrorResponse(Map<String, List<String>> errors) {
        super("Validation failed");
        this.errors = errors;
    }
}
