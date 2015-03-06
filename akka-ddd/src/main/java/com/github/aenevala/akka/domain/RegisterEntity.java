package com.github.aenevala.akka.domain;

public class RegisterEntity extends EntityMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public RegisterEntity(String id) {
        super(id);
    }
}
