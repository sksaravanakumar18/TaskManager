package com.cloudops.events.model;

import java.time.Instant;

public record TaskUpdatedEvent(
        String taskId,
        String title,
        String description,
        String assignee,
        String status,
        Instant updatedAt
) {
}
