package com.github.aenevala.akka.domain;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import com.github.aenevala.ddd.messaging.command.CommandMessage;
import org.junit.Test;

/**
 * Created by NevalaA on 30.7.2015.
 */
public class AggregateRootTest {

    @Test
    public void test() throws InterruptedException {
        ActorSystem system = ActorSystem.create("test");
        new JavaTestKit(system) {
            {
                ActorRef ref = system.actorOf(Props.create(DummyAggregateRoot.class), "test");
                ref.tell(new CommandMessage(new DummyAggregateRoot.DummyCommand("test")), getRef());
                ref.tell(new CommandMessage(new DummyAggregateRoot.DummyCommand("test")), getRef());
                ref.tell(new CommandMessage(new DummyAggregateRoot.DummyCommand("test")), getRef());
                ref.tell(new DummyAggregateRoot.DummyCommand("test"), getRef());
                Thread.sleep(1000L);

                watch(ref);
            }
        };

    }
}
