package com.cloudops.analyticsservice.config;

import com.cloudops.analyticsservice.model.TaskMetrics;
import com.cloudops.events.bus.InMemoryEventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public TaskMetrics taskMetrics() {
        return new TaskMetrics();
    }

    @Bean
    public InMemoryEventBus inMemoryEventBus() {
        return new InMemoryEventBus();
    }
}
