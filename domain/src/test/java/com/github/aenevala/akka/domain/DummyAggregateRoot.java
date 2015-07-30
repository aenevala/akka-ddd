package com.github.aenevala.akka.domain;

import akka.japi.pf.ReceiveBuilder;
import com.github.aenevala.ddd.messaging.command.Command;
import com.github.aenevala.ddd.messaging.event.DomainEvent;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

/**
 * Created by NevalaA on 30.7.2015.
 */
public class DummyAggregateRoot extends AggregateRoot {

    static class DummyCommand extends Command {


        public DummyCommand(final String entityId) {
            super(entityId);
        }
    }

    static class DummyEvent extends DomainEvent {

        public DummyEvent(final String aggregateId) {
            super(aggregateId);
        }
    }

    private static class DummyState implements AggregateState {

        @Override
        public DummyState apply(final DomainEvent event) {
            return this;
        }
    }

    @Override
    protected boolean canPassivate() {
        return false;
    }

    @Override
    protected AggregateRootFactory<DummyState> getAggregateRootFactory() {
        return x -> {
            if (x instanceof DummyEvent) {
                return new DummyState();
            } else {
                return null;
            }
        };
    }

    @Override
    protected PartialFunction<Object, BoxedUnit> processCommand() throws Exception {
        return ReceiveBuilder.match(DummyCommand.class, msg -> {
            log.debug("Process domain command {}", msg);
            raise(new DummyEvent(persistenceId()));
        }).build();
    }
}
