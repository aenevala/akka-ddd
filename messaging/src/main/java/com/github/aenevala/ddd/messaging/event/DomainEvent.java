package com.github.aenevala.ddd.messaging.event;

import java.io.Serializable;

/**
 * Created by NevalaA on 27.7.2015.
 */
public abstract class DomainEvent implements Serializable{

    private final String aggregateId;


    public DomainEvent(final String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getAggregateId() {
        return aggregateId;
    }
}
