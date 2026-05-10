package com.cloudops.events.bus;

import com.cloudops.events.model.DomainEvent;
import com.cloudops.events.publisher.EventPublisher;
import com.cloudops.events.subscriber.EventSubscriber;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryEventBus implements EventPublisher {

    private final ConcurrentHashMap<String, List<EventSubscriber>> subscribers = new ConcurrentHashMap<>();

    public void subscribe(String topic, EventSubscriber subscriber) {
        subscribers.computeIfAbsent(topic, k -> new ArrayList<>()).add(subscriber);
    }

    @Override
    public void publish(String topic, DomainEvent event) {
        List<EventSubscriber> topicSubscribers = subscribers.get(topic);
        if (topicSubscribers != null) {
            topicSubscribers.forEach(subscriber -> subscriber.onEvent(event));
        }
    }
}
