package com.github.aenevala.akka.domain.counter;

import akka.japi.Procedure;

import com.github.aenevala.akka.domain.AggregateRoot;
import com.github.aenevala.akka.domain.EntityMessage;
import com.github.aenevala.akka.domain.counter.CounterMessages.Increase;

public class Counter extends AggregateRoot {

    private int count = 0;

    public Counter() {
    }

    @Override
    public void processCommand(EntityMessage cmd) throws Exception {
        if (cmd instanceof Increase) {
            log.debug("Received increase, current system hash: {}", getContext().system().hashCode());
            persist(new CounterMessages.CountChanged(((Increase) cmd).getId(), count + 1), new Procedure<CounterMessages.CountChanged>() {

                public void apply(CounterMessages.CountChanged param) throws Exception {

                    count = param.getCount();
                    getContext().system().eventStream().publish(param);

                }
            });
        } else if (cmd instanceof CounterMessages.Decrease) {
            persist(new CounterMessages.CountChanged(((CounterMessages.Decrease) cmd).getId(), count - 1),
                    new Procedure<CounterMessages.CountChanged>() {

                        public void apply(CounterMessages.CountChanged param) throws Exception {
                            count = param.getCount();
                            getContext().system().eventStream().publish(param);

                        }
                    });
        } else if (cmd instanceof CounterMessages.Get) {
            sender().tell(new CounterMessages.GetReply(cmd.getId(), count), self());
        } else {
            super.unhandled(cmd);
        }

    }

    @Override
    public void processRecover(EntityMessage event) throws Exception {
        if (event instanceof CounterMessages.CountChanged) {
            count = ((CounterMessages.CountChanged) event).getCount();
        } else {
            unhandled(event);
        }

    }

    public String persistenceId() {
        return getSelf().path().parent().parent().name() + "-" + getSelf().path().name();
    }

}
