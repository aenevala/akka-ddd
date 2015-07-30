package com.github.aenevala.ddd.messaging.event;

import com.github.aenevala.ddd.messaging.EntityMessage;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by NevalaA on 29.7.2015.
 */
public class EventMessage extends EntityMessage<DomainEvent> {

    private final DomainEvent event;
    private final ZonedDateTime timestamp = ZonedDateTime.now(Clock.systemUTC());

    public EventMessage(final DomainEvent payload) {
        super(UUID.randomUUID().toString(), payload.getAggregateId(), payload);
        this.event = payload;
    }

    public DomainEvent getEvent() {
        return event;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
