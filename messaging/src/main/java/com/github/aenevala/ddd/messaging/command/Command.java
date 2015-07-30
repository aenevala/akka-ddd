package com.github.aenevala.ddd.messaging.command;

import java.io.Serializable;

/**
 * Created by NevalaA on 29.7.2015.
 */
public abstract class Command implements Serializable {

    private final String aggregateId;



    public Command(final String entityId) {
        this.aggregateId = entityId;
    }

    public String getAggregateId() {
        return aggregateId;
    }
}
