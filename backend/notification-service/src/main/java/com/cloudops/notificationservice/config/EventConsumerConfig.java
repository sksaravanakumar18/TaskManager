package com.cloudops.notificationservice.config;

import com.cloudops.events.bus.InMemoryEventBus;
import com.cloudops.events.pubsub.PubSubEventSubscriberAdapter;
import com.cloudops.notificationservice.consumer.TaskEventNotifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

public class EventConsumerConfig {

    private EventConsumerConfig() {}

    /** Local / test: wire notifier directly to InMemoryEventBus. */
    @Configuration
    @Profile("!gcp")
    static class LocalConsumerConfig {

        @Bean
        public TaskEventNotifier taskEventNotifier(InMemoryEventBus eventBus) {
            TaskEventNotifier notifier = new TaskEventNotifier();
            eventBus.subscribe("task-events", notifier);
            return notifier;
        }
    }

    /** GCP profile: subscribe to real Pub/Sub subscription. */
    @Configuration
    @Profile("gcp")
    static class GcpConsumerConfig {

        @Value("${pubsub.subscription.notification:notification-sub}")
        private String subscription;

        @Bean
        public TaskEventNotifier taskEventNotifier() {
            return new TaskEventNotifier();
        }

        @Bean
        public PubSubEventSubscriberAdapter notificationSubscriberAdapter(
                PubSubTemplate pubSubTemplate,
                TaskEventNotifier notifier,
                ObjectMapper objectMapper) {
            return new PubSubEventSubscriberAdapter(pubSubTemplate, subscription, notifier, objectMapper);
        }

        @Bean
        public PubSubStartup notificationStartup(PubSubEventSubscriberAdapter adapter) {
            return new PubSubStartup(adapter);
        }
    }

    record PubSubStartup(PubSubEventSubscriberAdapter adapter) {
        @PostConstruct void init() { adapter.start(); }
    }
}
