package com.cloudops.taskservice.config;

import com.cloudops.events.bus.InMemoryEventBus;
import com.cloudops.events.publisher.EventPublisher;
import com.cloudops.events.pubsub.PubSubEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

public class EventBusConfig {

    private EventBusConfig() {}

    /** Default / test profile: in-memory pub-sub, no GCP required. */
    @Configuration
    @Profile("!gcp")
    static class LocalEventBusConfig {

        @Bean
        public InMemoryEventBus inMemoryEventBus() {
            return new InMemoryEventBus();
        }

        @Bean
        public EventPublisher eventPublisher(InMemoryEventBus eventBus) {
            return eventBus;
        }
    }

    /** GCP / local-docker profiles: real Google Pub/Sub (or emulator). */
    @Configuration
    @Profile("gcp")
    static class GcpEventBusConfig {

        @Bean
        public EventPublisher eventPublisher(PubSubTemplate pubSubTemplate,
                                             ObjectMapper objectMapper) {
            return new PubSubEventPublisher(pubSubTemplate, objectMapper);
        }
    }
}
