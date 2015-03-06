package com.github.aenevala.akka.app;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSharding;
import akka.contrib.pattern.ShardRegion.MessageExtractor;
import akka.persistence.journal.leveldb.SharedLeveldbStore;

import com.github.aenevala.akka.domain.EntityMessage;
import com.github.aenevala.akka.domain.counter.CountChangedListener;
import com.github.aenevala.akka.domain.counter.Counter;
import com.github.aenevala.akka.persistence.SharedStorageUsage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Main {

	public static void main(String[] args) {
		// Start up two nodes with different ports
		Arrays.asList(2551, 2552).forEach((port) -> startUp(port));
	}

	private static void startUp(int port) {
		// Overwrite port in configuration
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).withFallback(ConfigFactory.load());
		// Create system
		ActorSystem system = ActorSystem.create("CounterSystem", config);
		// Register Counter region
		ClusterSharding.get(system).start("Counter", Props.create(Counter.class), getMessageExtractor());
		// Create Shared Level DB for one node only
		if (port == 2551) {
			@SuppressWarnings("unused")
			final ActorRef store = system.actorOf(Props.create(SharedLeveldbStore.class), "store");
		}

		// Register shared level db
		@SuppressWarnings("unused")
		final ActorRef usage = system.actorOf(Props.create(SharedStorageUsage.class), "usage");

		// Create listener for domain event (CountChanged)
	    @SuppressWarnings("unused")
		final ActorRef listener = system.actorOf(Props.create(CountChangedListener.class), "listener");

	    // Create counter region actor
		final ActorRef counterRegion = ClusterSharding.get(system).shardRegion("Counter");

		// Create sender
		@SuppressWarnings("unused")
		final ActorRef sender = system.actorOf(Props.create(Sender.class, counterRegion), "sender");

		int timeout = 15;
		// use different timeout for second node.
		if (port == 2552) {
			timeout = 10;
		}
        // let system run for [timeout] seconds before shutting down
		system.scheduler().scheduleOnce(FiniteDuration.create(timeout, TimeUnit.SECONDS), new Runnable() {
			
			@Override
			public void run() {
				system.shutdown();
				
			}
		}, system.dispatcher());
	}

	private static MessageExtractor getMessageExtractor() {
		return new MessageExtractor() {

			public String entryId(Object message) {
				if (message instanceof EntityMessage) {
					return ((EntityMessage) message).getId();
				} else {
					return null;
				}
			}

			public Object entryMessage(Object message) {
				// Do not convert message
				return message;
			}

			public String shardId(Object message) {
				if (message instanceof EntityMessage) {
					return Integer.toString(((EntityMessage) message).getId().hashCode() % 20);
				} else {
					return null;
				}
			}
		};
	}
}
