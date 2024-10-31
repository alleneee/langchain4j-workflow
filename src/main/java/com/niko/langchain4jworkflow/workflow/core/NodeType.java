package com.niko.langchain4jworkflow.workflow.core;

public enum NodeType {
    FUNCTION,   // 普通函数节点
    AI,         // AI处理节点
    CONDITIONAL, // 条件节点
    PARALLEL,   // 并行节点
    JOIN        // 汇聚节点
}
