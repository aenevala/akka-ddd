package com.github.aenevala.akka.domain;

public class AggregateStateChanged extends EntityMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final EntityState state;

    public AggregateStateChanged(String id, EntityState state) {
        super(id);
        this.state = state;
    }

    public final EntityState getState() {
        return state;
    }

}
