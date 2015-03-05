package com.github.aenevala.akka.domain;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Sender extends UntypedActor {
	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private ActorRef counterRegion;
	
	private Random random = new Random();

	public Sender(ActorRef counterRegion) {
		this.counterRegion = counterRegion;
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		context()
				.system()
				.scheduler()
				.schedule(FiniteDuration.apply(5, TimeUnit.SECONDS),
						FiniteDuration.create(100, TimeUnit.MILLISECONDS), getSelf(),
						"increase", context().dispatcher(), getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		
		if (message.equals("increase")) {
			String id = Integer.toString(random.nextInt(1000));
			log.info("Sending increase command to {} {}", counterRegion, id);
			counterRegion.tell(new Counter.Increase(id), self());
			counterRegion.tell(new Counter.Get(id), self());
		} else {
			unhandled(message);
		}

	}

}
