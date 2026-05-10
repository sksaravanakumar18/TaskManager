package com.cloudops.analyticsservice.config;

import com.cloudops.analyticsservice.consumer.TaskEventConsumer;
import com.cloudops.analyticsservice.model.TaskMetrics;
import com.cloudops.events.bus.InMemoryEventBus;
import com.cloudops.events.pubsub.PubSubEventSubscriberAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

public class EventConsumerConfig {

    private EventConsumerConfig() {}

    /** Local / test: wire consumer directly to InMemoryEventBus. */
    @Configuration
    @Profile("!gcp")
    static class LocalConsumerConfig {

        @Bean
        public TaskEventConsumer taskEventConsumer(InMemoryEventBus eventBus, TaskMetrics metrics) {
            TaskEventConsumer consumer = new TaskEventConsumer(metrics);
            eventBus.subscribe("task-events", consumer);
            return consumer;
        }
    }

    /** GCP profile: subscribe to real Pub/Sub subscription. */
    @Configuration
    @Profile("gcp")
    static class GcpConsumerConfig {

        @Value("${pubsub.subscription.analytics:analytics-sub}")
        private String subscription;

        @Bean
        public TaskEventConsumer taskEventConsumer(TaskMetrics metrics) {
            return new TaskEventConsumer(metrics);
        }

        @Bean
        public PubSubEventSubscriberAdapter analyticsSubscriberAdapter(
                PubSubTemplate pubSubTemplate,
                TaskEventConsumer consumer,
                ObjectMapper objectMapper) {
            return new PubSubEventSubscriberAdapter(pubSubTemplate, subscription, consumer, objectMapper);
        }

        @Bean
        public PubSubStartup analyticsStartup(PubSubEventSubscriberAdapter adapter) {
            return new PubSubStartup(adapter);
        }
    }

    /** Helper that calls adapter.start() after the context is ready. */
    record PubSubStartup(PubSubEventSubscriberAdapter adapter) {
        @PostConstruct void init() { adapter.start(); }
    }
}
