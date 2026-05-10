package com.cloudops.events.pubsub;

import com.cloudops.events.model.DomainEvent;
import com.cloudops.events.publisher.EventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Google Cloud Pub/Sub implementation of {@link EventPublisher}.
 *
 * <p>Serialises the {@link DomainEvent} to JSON and publishes it to the
 * Pub/Sub topic whose name matches the {@code topic} parameter. Supports
 * the local Pub/Sub emulator transparently via the
 * {@code PUBSUB_EMULATOR_HOST} environment variable.
 */
public class PubSubEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PubSubEventPublisher.class);

    private final PubSubTemplate pubSubTemplate;
    private final ObjectMapper   objectMapper;

    public PubSubEventPublisher(PubSubTemplate pubSubTemplate, ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.objectMapper   = objectMapper;
    }

    @Override
    public void publish(String topic, DomainEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            pubSubTemplate.publish(topic, json)
                .whenComplete((messageId, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {} to topic {}: {}",
                                  event.eventType(), topic, ex.getMessage(), ex);
                    } else {
                        log.info("Published event {} to topic {} (msgId={})",
                                 event.eventType(), topic, messageId);
                    }
                });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialise DomainEvent to JSON", e);
        }
    }
}
