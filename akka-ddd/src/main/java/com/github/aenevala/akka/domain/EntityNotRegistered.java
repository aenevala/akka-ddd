package com.github.aenevala.akka.domain;

public class EntityNotRegistered extends EntityMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public EntityNotRegistered(String id) {
        super(id);
    }
}
