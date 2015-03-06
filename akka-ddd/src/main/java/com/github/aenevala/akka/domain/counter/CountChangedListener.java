package com.github.aenevala.akka.domain.counter;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class CountChangedListener extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public CountChangedListener() {
        getContext().system().eventStream().subscribe(getSelf(), CounterMessages.CountChanged.class);
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof CounterMessages.CountChanged) {
            log.info("Received count changed from {}, new count {}", ((CounterMessages.CountChanged) msg).getId(),
                    ((CounterMessages.CountChanged) msg).getCount());
        }

    }

}
