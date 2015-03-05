package com.github.aenevala.akka.domain;

import com.github.aenevala.akka.domain.Counter.CountChanged;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class CountChangedListener extends UntypedActor {
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	public CountChangedListener() {
		getContext().system().eventStream().subscribe(getSelf(), CountChanged.class);
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof CountChanged) {
			log.info("Received count changed from {}, new count {}", ((CountChanged) msg).getId(), ((CountChanged) msg).getCount());
		}
		
	}

}
