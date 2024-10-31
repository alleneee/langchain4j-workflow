package com.niko.langchain4jworkflow.workflow.exceptions;

import java.util.List;
import java.util.Map;

/**
 * 验证异常
 */
public class ValidationException extends WorkflowException {

    private final Map<String, List<String>> validationErrors;

    public ValidationException(Map<String, List<String>> validationErrors) {
        super("Validation failed: " + validationErrors);
        this.validationErrors = validationErrors;
    }

    public Map<String, List<String>> getValidationErrors() {
        return validationErrors;
    }
}

