package com.github.aenevala.ddd.messaging;

public abstract class EntityMessage<T> extends Message {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String entityId;

    private T payload;



    public EntityMessage(String id, String entityId, T payload) {
        super(id);
        this.entityId = entityId;
        this.payload = payload;
    }

    public String getEntityId() {
        return entityId;

    }


    public T getPayload() {
        return payload;
    }
}