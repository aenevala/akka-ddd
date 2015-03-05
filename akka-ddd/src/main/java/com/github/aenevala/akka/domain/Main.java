package com.github.aenevala.akka.domain;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.contrib.pattern.ClusterSharding;
import akka.contrib.pattern.ShardRegion.MessageExtractor;
import akka.persistence.journal.leveldb.SharedLeveldbStore;

import com.github.aenevala.akka.domain.Counter.AggregateMessage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Main {

	public static void main(String[] args) {
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
		system.scheduler().scheduleOnce(FiniteDuration.create(100, TimeUnit.SECONDS), new Runnable() {
			
			@Override
			public void run() {
				system.shutdown();
				
			}
		}, system.dispatcher());
	}

	private static MessageExtractor getMessageExtractor() {
		return new MessageExtractor() {

			public String entryId(Object message) {
				if (message instanceof AggregateMessage) {
					return ((AggregateMessage) message).getId();
				} else {
					return null;
				}
			}

			public Object entryMessage(Object message) {
				return message;
			}

			public String shardId(Object message) {
				if (message instanceof AggregateMessage) {
					return Integer.toString(((AggregateMessage) message).getId().hashCode() % 20);
				} else {
					return null;
				}
			}
		};
	}
}
