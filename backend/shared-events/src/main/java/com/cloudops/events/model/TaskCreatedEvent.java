package com.cloudops.events.model;

import java.time.Instant;

public record TaskCreatedEvent(
        String taskId,
        String title,
        String description,
        String assignee,
        String status,
        Instant createdAt
) {
}
