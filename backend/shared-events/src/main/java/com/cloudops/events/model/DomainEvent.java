package com.cloudops.events.model;

import java.time.Instant;

public record DomainEvent(
        String eventId,
        String eventType,
        Instant timestamp,
        Object payload
) {
    public static DomainEvent of(String eventType, Object payload) {
        return new DomainEvent(
                java.util.UUID.randomUUID().toString(),
                eventType,
                Instant.now(),
                payload
        );
    }
}
