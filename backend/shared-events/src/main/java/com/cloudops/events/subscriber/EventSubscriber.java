package com.cloudops.events.subscriber;

import com.cloudops.events.model.DomainEvent;

public interface EventSubscriber {
    void onEvent(DomainEvent event);
}
