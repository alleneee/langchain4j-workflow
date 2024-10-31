package com.niko.langchain4jworkflow;

import com.niko.langchain4jworkflow.workflow.model.CreateWorkflowRequest;
import com.niko.langchain4jworkflow.workflow.model.ExecuteWorkflowRequest;
import com.niko.langchain4jworkflow.workflow.model.WorkflowResponse;
import com.niko.langchain4jworkflow.workflow.service.WorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class Langchain4jWorkflowApplicationTests {

    @Autowired
    private WorkflowService workflowService;

    @Test
    void contextLoads() {
    }

}
