package com.niko.langchain4jworkflow.workflow.core;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class WorkflowState implements Cloneable {
    private final String workflowId;
    private final Map<String, Object> variables;
    private final Map<String, Object> executionHistory;
    private WorkflowStatus status;
    private String errorMessage;

    public WorkflowState() {
        this(UUID.randomUUID().toString());
    }

    public WorkflowState(String workflowId) {
        this.workflowId = workflowId;
        this.variables = new ConcurrentHashMap<>();
        this.executionHistory = new ConcurrentHashMap<>();
        this.status = WorkflowStatus.RUNNING;
    }

    public void markAsCompleted() {
        this.status = WorkflowStatus.COMPLETED;
    }

    public void markAsFailed(String message) {
        this.status = WorkflowStatus.FAILED;
        this.errorMessage = message;
    }

    public boolean isNodeCompleted(String nodeName) {
        return executionHistory.containsKey(nodeName);
    }

    public void recordNodeStart(String nodeName) {
        executionHistory.put(nodeName + ".start", System.currentTimeMillis());
    }

    public void recordNodeCompletion(String nodeName) {
        executionHistory.put(nodeName + ".end", System.currentTimeMillis());
    }

    // 修改这里，接受Throwable类型
    public void recordNodeError(String nodeName, Throwable error) {
        executionHistory.put(nodeName + ".error", error.getMessage());
    }

    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }

    public Object getVariable(String key) {
        return variables.get(key);
    }

    public <T> T getVariable(String key, Class<T> type) {
        Object value = variables.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public boolean hasVariable(String key) {
        return variables.containsKey(key);
    }

    public void removeVariable(String key) {
        variables.remove(key);
    }

    @Override
    public WorkflowState clone() {
        WorkflowState cloned = new WorkflowState(this.workflowId);
        cloned.variables.putAll(this.variables);
        cloned.executionHistory.putAll(this.executionHistory);
        cloned.status = this.status;
        cloned.errorMessage = this.errorMessage;
        return cloned;
    }
}