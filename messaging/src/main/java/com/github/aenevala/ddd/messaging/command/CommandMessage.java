package com.github.aenevala.ddd.messaging.command;

import com.github.aenevala.ddd.messaging.EntityMessage;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Created by NevalaA on 29.7.2015.
 */
public class CommandMessage extends EntityMessage<Command> {

    private final Command command;
    private final ZonedDateTime timestamp = ZonedDateTime.now(Clock.systemUTC());

    public CommandMessage(Command command) {
        super(UUID.randomUUID().toString(), command.getAggregateId(), command);
        this.command = command;

    }

    public Command getCommand() {
        return command;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }
}
