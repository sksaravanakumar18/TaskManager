package com.cloudops.events.publisher;

import com.cloudops.events.model.DomainEvent;

public interface EventPublisher {
    void publish(String topic, DomainEvent event);
}
