package com.niko.langchain4jworkflow.workflow.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishEvent(Object event) {
        publisher.publishEvent(event);
    }
}
