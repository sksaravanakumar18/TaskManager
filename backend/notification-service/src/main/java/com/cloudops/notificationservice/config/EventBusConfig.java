package com.cloudops.notificationservice.config;

import com.cloudops.events.bus.InMemoryEventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfig {

    @Bean
    public InMemoryEventBus inMemoryEventBus() {
        return new InMemoryEventBus();
    }
}
