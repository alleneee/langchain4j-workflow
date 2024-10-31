package com.niko.langchain4jworkflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.niko.langchain4jworkflow.workflow.config.WorkflowProperties;

@SpringBootApplication
@EnableConfigurationProperties(WorkflowProperties.class)
public class WorkflowApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
    }
}
