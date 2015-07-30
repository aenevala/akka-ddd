package com.github.aenevala.ddd.messaging;

import java.io.Serializable;

/**
 * Created by NevalaA on 29.7.2015.
 */
public abstract class Message implements Serializable {

    private Header header;
    private final String id;

    public Message(final String id) {
        this.header = new Header(null, null, null);
        this.id = id;
    }

    public Header getHeader() {
        return header;
    }

    public String getId() {
        return id;
    }

    public Message withCauseId(String causeId) {
        this.header = new Header(deliveryId(), causeId, correlationId());
        return this;
    }

    public Message withCorrelationId(String correlationId) {
        this.header = new Header(deliveryId(), causeId(), correlationId);
        return this;
    }

    public Message withDeliveryId(String deliveryId) {
        this.header = new Header(deliveryId, causeId(), correlationId());
        return this;
    }

    public Message causedBy(Message msg) {
        this.header = new Header(deliveryId(), msg.getId(), msg.correlationId());
        return this;
    }

    public String correlationId() {
        return header.getCorrelationId();
    }

    public String causeId() {
        return header.getCauseId();
    }

    public String deliveryId() {
        return header.getDeliveryId();
    }

}
