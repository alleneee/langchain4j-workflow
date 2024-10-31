package com.niko.langchain4jworkflow.workflow.core;

import lombok.Builder;
import lombok.Data;
import org.springframework.context.ApplicationContext;

/**
 * 工作流上下文
 * 包含工作流执行过程中的上下文信息，如工作流ID、名称和状态等
 */
@Data
@Builder
public class WorkflowContext {
    /**
     * 工作流ID
     */
    private final String workflowId;
    
    /**
     * 工作流名称
     */
    private final String workflowName;
    
    /**
     * 工作流状态
     */
    private final WorkflowState state;

    /**
     * Spring应用上下文
     */
    private final ApplicationContext applicationContext;

    /**
     * 获取Bean实例
     * @param type Bean类型
     * @return Bean实例
     */
    public <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }

    /**
     * 获取指定名称的Bean实例
     * @param name Bean名称
     * @param type Bean类型
     * @return Bean实例
     */
    public <T> T getBean(String name, Class<T> type) {
        return applicationContext.getBean(name, type);
    }
}
