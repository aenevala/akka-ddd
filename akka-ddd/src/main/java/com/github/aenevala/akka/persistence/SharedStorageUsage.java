package com.github.aenevala.akka.persistence;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Identify;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.persistence.journal.leveldb.SharedLeveldbJournal;

public class SharedStorageUsage extends UntypedActor {
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    @Override
    public void preStart() throws Exception {
        String path = "akka.tcp://CounterSystem@127.0.0.1:2551/user/store";
        ActorSelection selection = getContext().actorSelection(path);
        selection.tell(new Identify(1), getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ActorIdentity) {
            ActorIdentity identity = (ActorIdentity) message;
            if (identity.correlationId().equals(1)) {
                ActorRef store = identity.getRef();
                if (store != null) {
                    SharedLeveldbJournal.setStore(store, getContext().system());
                } else {
                    log.error("Shared Level DB not found");
                    context().system().shutdown();
                }
            }
        }
    }
}
