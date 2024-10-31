package com.niko.langchain4jworkflow.workflow.core;

import lombok.Data;
import lombok.Builder;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工作流状态
 * 维护工作流执行过程中的状态信息，包括变量、执行历史等
 */
@Data
public class WorkflowState implements Cloneable {
    /**
     * 工作流ID
     */
    private final String workflowId;

    /**
     * 工作流变量
     */
    private final Map<String, Object> variables;

    /**
     * 执行历史记录
     */
    private final Map<String, NodeExecutionInfo> executionHistory;

    /**
     * 工作流状态
     */
    private WorkflowStatus status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 开始时间
     */
    private final Instant startTime;

    /**
     * 结束时间
     */
    private Instant endTime;

    /**
     * 节点执行信息
     */
    @Data
    @Builder
    public static class NodeExecutionInfo {
        private final String nodeName;
        private final Map<String, Object> outputs;
        private final int attempts;
        private final String error;
        private final Instant startTime;
        private final Instant endTime;

        /**
         * 获取执行持续时间
         */
        public Duration getDuration() {
            return endTime != null ? 
                Duration.between(startTime, endTime) : 
                Duration.ZERO;
        }
    }

    /**
     * 默认构造函数
     */
    public WorkflowState() {
        this(UUID.randomUUID().toString());
    }

    /**
     * 带ID的构造函数
     */
    public WorkflowState(String workflowId) {
        this.workflowId = workflowId;
        this.variables = new ConcurrentHashMap<>();
        this.executionHistory = new ConcurrentHashMap<>();
        this.status = WorkflowStatus.RUNNING;
        this.startTime = Instant.now();
    }

    /**
     * 获取变量值
     * @param key 变量名
     * @return 变量值
     */
    public Object getVariable(String key) {
        return variables.get(key);
    }

    /**
     * 获取指定类型的变量值
     * @param key 变量名
     * @param type 变量类型
     * @return 变量值
     */
    public <T> T getVariable(String key, Class<T> type) {
        Object value = variables.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    /**
     * 设置变量值
     * @param key 变量名
     * @param value 变量值
     */
    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }

    /**
     * 检查变量是否存在
     * @param key 变量名
     * @return 是否存在
     */
    public boolean hasVariable(String key) {
        return variables.containsKey(key);
    }

    /**
     * 移除变量
     * @param key 变量名
     */
    public void removeVariable(String key) {
        variables.remove(key);
    }

    /**
     * 标记工作流为完成状态
     */
    public void markAsCompleted() {
        this.status = WorkflowStatus.COMPLETED;
        this.endTime = Instant.now();
    }

    /**
     * 标记工作流为失败状态
     */
    public void markAsFailed(String message) {
        this.status = WorkflowStatus.FAILED;
        this.errorMessage = message;
        this.endTime = Instant.now();
    }

    /**
     * 检查节点是否已完成
     */
    public boolean isNodeCompleted(String nodeName) {
        return executionHistory.containsKey(nodeName);
    }

    /**
     * 记录节点开始执行
     */
    public void recordNodeStart(String nodeName) {
        NodeExecutionInfo info = NodeExecutionInfo.builder()
                .nodeName(nodeName)
                .startTime(Instant.now())
                .attempts(1)
                .outputs(new HashMap<>())
                .build();
        executionHistory.put(nodeName, info);
    }

    /**
     * 记录节点执行完成
     */
    public void recordNodeCompletion(String nodeName, Map<String, Object> outputs) {
        NodeExecutionInfo existing = executionHistory.get(nodeName);
        if (existing != null) {
            NodeExecutionInfo updated = NodeExecutionInfo.builder()
                    .nodeName(nodeName)
                    .outputs(outputs)
                    .attempts(existing.getAttempts())
                    .startTime(existing.getStartTime())
                    .endTime(Instant.now())
                    .build();
            executionHistory.put(nodeName, updated);
        }
    }

    /**
     * 记录节点执行错误
     */
    public void recordNodeError(String nodeName, Throwable error) {
        NodeExecutionInfo existing = executionHistory.get(nodeName);
        if (existing != null) {
            NodeExecutionInfo updated = NodeExecutionInfo.builder()
                    .nodeName(nodeName)
                    .outputs(existing.getOutputs())
                    .attempts(existing.getAttempts() + 1)
                    .error(error.getMessage())
                    .startTime(existing.getStartTime())
                    .endTime(Instant.now())
                    .build();
            executionHistory.put(nodeName, updated);
        }
    }

    /**
     * 获取执行持续时间
     */
    public Duration getDuration() {
        return endTime != null ? 
            Duration.between(startTime, endTime) : 
            Duration.between(startTime, Instant.now());
    }

    @Override
    public WorkflowState clone() {
        WorkflowState cloned = new WorkflowState(this.workflowId);
        cloned.variables.putAll(this.variables);
        cloned.executionHistory.putAll(this.executionHistory);
        cloned.status = this.status;
        cloned.errorMessage = this.errorMessage;
        cloned.endTime = this.endTime;
        return cloned;
    }
}