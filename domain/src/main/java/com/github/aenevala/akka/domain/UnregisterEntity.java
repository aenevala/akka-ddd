package com.github.aenevala.akka.domain;

public class UnregisterEntity extends EntityMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public UnregisterEntity(String id) {
        super(id);
    }
}
