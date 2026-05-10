package com.cloudops.events.pubsub;

import com.cloudops.events.model.DomainEvent;
import com.cloudops.events.subscriber.EventSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bridges a Google Cloud Pub/Sub subscription to an {@link EventSubscriber}.
 *
 * <p>Call {@link #start()} once after the bean is initialised. Messages are
 * deserialised from JSON into a {@link DomainEvent} and forwarded to the
 * provided subscriber. Malformed messages are immediately acknowledged (dead-
 * lettered by Pub/Sub after max delivery attempts).
 */
public class PubSubEventSubscriberAdapter {

    private static final Logger log = LoggerFactory.getLogger(PubSubEventSubscriberAdapter.class);

    private final PubSubTemplate  pubSubTemplate;
    private final String          subscription;
    private final EventSubscriber subscriber;
    private final ObjectMapper    objectMapper;

    public PubSubEventSubscriberAdapter(PubSubTemplate pubSubTemplate,
                                        String subscription,
                                        EventSubscriber subscriber,
                                        ObjectMapper objectMapper) {
        this.pubSubTemplate = pubSubTemplate;
        this.subscription   = subscription;
        this.subscriber     = subscriber;
        this.objectMapper   = objectMapper;
    }

    /** Register an async pull listener on the Pub/Sub subscription. */
    public void start() {
        log.info("Subscribing to Pub/Sub subscription: {}", subscription);
        pubSubTemplate.subscribe(subscription, this::handleMessage);
    }

    private void handleMessage(BasicAcknowledgeablePubsubMessage message) {
        String payload = message.getPubsubMessage().getData().toStringUtf8();
        try {
            DomainEvent event = objectMapper.readValue(payload, DomainEvent.class);
            subscriber.onEvent(event);
            message.ack();
        } catch (Exception e) {
            log.error("Failed to process Pub/Sub message from {}: {}", subscription, e.getMessage(), e);
            message.nack(); // let Pub/Sub retry / dead-letter
        }
    }
}
