package com.github.aenevala.akka.domain;

import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.PoisonPill;
import akka.actor.ReceiveTimeout;
import akka.contrib.pattern.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.persistence.UntypedPersistentActor;

public abstract class AggregateRoot extends UntypedPersistentActor {

    protected LoggingAdapter log = Logging.getLogger(context().system(), this);

    private EntityState state = EntityState.INITIAL;

    protected abstract void processCommand(EntityMessage msg) throws Exception;

    protected abstract void processRecover(EntityMessage msg) throws Exception;

    @Override
    public String persistenceId() {
        return getSelf().path().parent().parent().name() + "-" + getSelf().path().name();

    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        log.info("Starting up {}", persistenceId());
        context().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));
    };

    @Override
    public void postStop() {
        log.info("Stopping {}", persistenceId());
        super.postStop();
    }

    @Override
    public void onReceiveCommand(Object msg) throws Exception {
        if (msg.equals(ReceiveTimeout.getInstance())) {
            getContext().parent().tell(new ShardRegion.Passivate(PoisonPill.getInstance()), getSelf());
        } else if (msg instanceof RegisterEntity) {
            if (state != EntityState.REGISTERED) {
                persist(new AggregateStateChanged(((RegisterEntity) msg).getId(), EntityState.REGISTERED), changeState());
            }
        } else if (msg instanceof UnregisterEntity) {
            persist(new AggregateStateChanged(((UnregisterEntity) msg).getId(), EntityState.REMOVED), changeState());
        } else if (msg instanceof EntityMessage) {
            if (state == EntityState.REGISTERED) {
                this.processCommand((EntityMessage) msg);
            } else {
                log.warning("Entity {} not registered", ((EntityMessage) msg).getId());
                sender().tell(new EntityNotRegistered(((EntityMessage) msg).getId()), self());
            }
        } else {
            unhandled(msg);
        }
    }

    private Procedure<AggregateStateChanged> changeState() {
        return new Procedure<AggregateStateChanged>() {

            @Override
            public void apply(AggregateStateChanged stateChanged) throws Exception {
                updateState(stateChanged.getState());
            }
        };
    }

    @Override
    public void onReceiveRecover(Object msg) throws Exception {
        if (msg instanceof AggregateStateChanged) {
            updateState(((AggregateStateChanged) msg).getState());
        } else if (msg instanceof EntityMessage) {
            processRecover((EntityMessage) msg);
        } else {
            unhandled(msg);
        }
    }

    private void updateState(EntityState state) {
        this.state = state;
    }
}
