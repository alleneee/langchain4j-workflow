package com.niko.langchain4jworkflow.workflow.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> errors;

    public ValidationErrorResponse(int status, String error, Map<String, String> errors) {
        super(status, error, "Validation failed");
        this.errors = errors;
    }
}
