package com.github.aenevala.ddd.messaging;

import java.io.Serializable;

/**
 * Created by NevalaA on 29.7.2015.
 */
public class Header implements Serializable {

    private final String deliveryId;
    private final String causeId;
    private final String correlationId;

    public Header(final String deliveryId, final String causeId, final String correlationId) {
        this.deliveryId = deliveryId;
        this.causeId = causeId;
        this.correlationId = correlationId;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public String getCauseId() {
        return causeId;
    }

    public String getCorrelationId() {
        return correlationId;
    }
}
