package com.cloudops.events.config;

import com.cloudops.events.bus.InMemoryEventBus;
import com.cloudops.events.publisher.EventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfig {

    @Bean
    public InMemoryEventBus inMemoryEventBus() {
        return new InMemoryEventBus();
    }

    @Bean
    public EventPublisher eventPublisher(InMemoryEventBus eventBus) {
        return eventBus;
    }
}
