package com.cloudops.analyticsservice.consumer;

import com.cloudops.analyticsservice.model.TaskMetrics;
import com.cloudops.events.model.DomainEvent;
import com.cloudops.events.subscriber.EventSubscriber;
import org.springframework.stereotype.Component;

public class TaskEventConsumer implements EventSubscriber {

    private final TaskMetrics metrics;

    public TaskEventConsumer(TaskMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public void onEvent(DomainEvent event) {
        switch (event.eventType()) {
            case "task.created" -> metrics.recordCreated();
            case "task.updated" -> {
                Object payload = event.payload();
                if (payload instanceof com.cloudops.events.model.TaskUpdatedEvent updated) {
                    metrics.recordUpdated(updated.status());
                }
            }
            case "task.deleted" -> metrics.recordDeleted();
        }
    }
}
