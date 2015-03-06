package com.github.aenevala.akka.domain;

import java.io.Serializable;

public abstract class EntityMessage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String id;

    public EntityMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;

    }
}