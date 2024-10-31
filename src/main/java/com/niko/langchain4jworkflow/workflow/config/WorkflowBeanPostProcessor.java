package com.niko.langchain4jworkflow.workflow.config;


import com.niko.langchain4jworkflow.workflow.annotation.Workflow;
import com.niko.langchain4jworkflow.workflow.core.WorkflowRegistry;
import com.niko.langchain4jworkflow.workflow.core.WorkflowScanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
@RequiredArgsConstructor
public class WorkflowBeanPostProcessor implements BeanPostProcessor {

    private final WorkflowRegistry registry;
    private final WorkflowScanner scanner;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(Workflow.class)) {
            log.debug("Found workflow bean: {}", beanName);
            scanner.scanWorkflow(clazz)
                    .ifPresent(workflow -> {
                        log.info("Registering workflow: {}", workflow.getName());
                        registry.register(workflow);
                    });
        }
        return bean;
    }
}