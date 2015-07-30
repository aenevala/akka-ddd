package com.github.aenevala.akka.domain;

import akka.actor.PoisonPill;
import akka.actor.ReceiveTimeout;
import akka.cluster.sharding.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Option;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.AbstractPersistentActor;
import com.github.aenevala.akka.domain.error.AggregateRootNotInitializedException;
import com.github.aenevala.ddd.messaging.command.Command;
import com.github.aenevala.ddd.messaging.command.CommandMessage;
import com.github.aenevala.ddd.messaging.event.DomainEvent;
import com.github.aenevala.ddd.messaging.event.EventMessage;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;

import java.util.concurrent.TimeUnit;

public abstract class AggregateRoot<T extends AggregateState<T>> extends AbstractPersistentActor {

    private Option<T> stateOpt = Option.none();
    protected LoggingAdapter log = Logging.getLogger(context().system(), this);
    private CommandMessage lastCommandMessage;

    protected abstract boolean canPassivate();

    protected abstract AggregateRootFactory<T> getAggregateRootFactory();

    protected abstract PartialFunction<Object, BoxedUnit> processCommand() throws Exception;

    protected void raise(DomainEvent event) {
        log.debug("Persist event {} with {}", event, persistenceId());
        EventMessage eventMessage = new EventMessage(event);
        eventMessage.causedBy(lastCommandMessage);
        persist(eventMessage, msg -> {
            updateState(msg.getEvent());
        });
    }

    protected void updateState(final DomainEvent event) {
        T nextState;
        if (initialized()) {
            log.debug("Passing event {} to state {}", event, state());
            nextState = state().apply(event);
        } else {
            log.debug("Passing event {} to factory {}", event, getAggregateRootFactory());
            nextState = getAggregateRootFactory().apply(event);
        }
        stateOpt = Option.option(nextState);
    }

    protected boolean initialized() {
        return stateOpt.isDefined();
    }


    @SuppressWarnings("unused")
    public String getAggregateId() {
        return persistenceId();
    }

    @Override
    public void postStop() {
        log.info("Stopping {}", persistenceId());
        super.postStop();
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        log.info("Starting up {}", persistenceId());
        context().setReceiveTimeout(getReceiveTimeout());
    }

    @Override
    public String persistenceId() {
        return self().path().parent().name() + "-" + self().path().name();

    }

    protected Duration getReceiveTimeout() {
        return Duration.create(60, TimeUnit.SECONDS);
    }

    @Override
    public void unhandled(Object message) {
        log.warning("Unhandled: {}", message);
        if (message instanceof ReceiveTimeout) {
            if (canPassivate()) {
                context().parent().tell(new ShardRegion.Passivate(PoisonPill.getInstance()), self());
            }
        } else {
            super.unhandled(message);
        }
    }

    private T state() {
        if (initialized()) {
            return stateOpt.get();
        } else {
            throw new AggregateRootNotInitializedException();
        }
    }



    @Override
    public PartialFunction<Object, BoxedUnit> receiveRecover() {
        return ReceiveBuilder.match(EventMessage.class, msg -> {
            log.debug("Recovering event {} {}", msg.getEvent(), msg.getTimestamp());
            updateState(msg.getEvent());
        }).build();
    }

    @Override
    public PartialFunction<Object, BoxedUnit> receiveCommand() {
        return ReceiveBuilder.match(CommandMessage.class, cmd -> {
            log.info("Received {}", cmd);
            lastCommandMessage = cmd;
            processCommand().applyOrElse(cmd.getCommand(), new AbstractFunction1<Command, Object>() {
                @Override
                public Object apply(final Command v1) {
                    unhandled(cmd);
                    return null;
                }
            });
        }).build();
    }

}
