package com.cloudops.notificationservice.consumer;

import com.cloudops.events.model.DomainEvent;
import com.cloudops.events.subscriber.EventSubscriber;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class TaskEventNotifier implements EventSubscriber {

    private final List<String> notifications = new ArrayList<>();

    @Override
    public void onEvent(DomainEvent event) {
        String message = switch (event.eventType()) {
            case "task.created" -> "📋 New task created: " + event.eventId();
            case "task.updated" -> "✏️ Task updated: " + event.eventId();
            case "task.deleted" -> "🗑️ Task deleted: " + event.eventId();
            default -> "Event: " + event.eventType();
        };
        notifications.add(message);
        System.out.println("[NOTIFICATION] " + message);
    }

    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public void clearNotifications() {
        notifications.clear();
    }
}
