package com.niko.langchain4jworkflow.workflow.model;

@Data
@Builder
public class NodeExecutionResponse {
    private String nodeName;
    private String status;
    private Map<String, Object> outputs;
    private Integer attempts;
    private String error;
    private Instant startTime;
    private Instant endTime;
    private Long duration;
}
