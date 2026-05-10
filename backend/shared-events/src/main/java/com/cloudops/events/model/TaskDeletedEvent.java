package com.cloudops.events.model;

import java.time.Instant;

public record TaskDeletedEvent(
        String taskId,
        Instant deletedAt
) {
}
