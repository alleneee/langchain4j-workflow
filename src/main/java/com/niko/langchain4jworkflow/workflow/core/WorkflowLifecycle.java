package com.niko.langchain4jworkflow.workflow.core;


import com.niko.langchain4jworkflow.workflow.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowLifecycle {

    @EventListener
    public void onWorkflowStart(WorkflowStartEvent event) {
        // 处理工作流开始事件
    }

    @EventListener
    public void onWorkflowComplete(WorkflowCompleteEvent event) {
        // 处理工作流完成事件
    }

    @EventListener
    public void onWorkflowError(WorkflowErrorEvent event) {
        // 处理工作流错误事件
    }

    @EventListener
    public void onNodeStart(NodeStartEvent event) {
        // 处理节点开始事件
    }

    @EventListener
    public void onNodeComplete(NodeCompleteEvent event) {
        // 处理节点完成事件
    }

    @EventListener
    public void onNodeError(NodeErrorEvent event) {
        // 处理节点错误事件
    }
}
