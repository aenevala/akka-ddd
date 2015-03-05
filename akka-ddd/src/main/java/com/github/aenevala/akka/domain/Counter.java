package com.github.aenevala.akka.domain;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.PoisonPill;
import akka.actor.ReceiveTimeout;
import akka.contrib.pattern.ShardRegion;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.persistence.UntypedPersistentActor;

public class Counter extends UntypedPersistentActor {

	public abstract static class AggregateMessage implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5928300563689832074L;
		private String id;

		public AggregateMessage(String id) {
			this.id = id;
		}

		public String getId() {
			return id;

		}
	}

	public static class Increase extends AggregateMessage implements
			Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4121922024514233771L;

		public Increase(String id) {
			super(id);
		}

	}

	public static class Decrease extends AggregateMessage implements
			Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 255131936506429690L;

		public Decrease(String id) {
			super(id);
		}

	}

	public static class Get extends AggregateMessage implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7869300410793718633L;

		public Get(String id) {
			super(id);
		}

	}

	public static class CountChanged extends AggregateMessage implements
			Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 726215547535187405L;
		private final int delta;

		public CountChanged(String id, int delta) {
			super(id);
			this.delta = delta;
		}

		public int getCount() {
			return delta;
		}

	}

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private int count = 0;

	public Counter() {
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
	public void onReceiveCommand(Object cmd) throws Exception {
		if (cmd instanceof Increase) {
			log.info("Received increase shard {}", getContext().system().hashCode());
			persist(new CountChanged(((Increase) cmd).getId(), count + 1),
					new Procedure<CountChanged>() {

						public void apply(CountChanged param) throws Exception {

							count = param.getCount();
							getContext().system().eventStream().publish(param);

						}
					});
		} else if (cmd instanceof Decrease) {
			persist(new CountChanged(((Decrease) cmd).getId(), count - 1),
					new Procedure<CountChanged>() {

						public void apply(CountChanged param) throws Exception {
							count = param.getCount();
							getContext().system().eventStream().publish(param);

						}
					});
		} else if (cmd instanceof Get) {
			sender().tell(count, self());
		} else if (cmd.equals(ReceiveTimeout.getInstance())) {
			getContext().parent().tell(
					new ShardRegion.Passivate(PoisonPill.getInstance()),
					getSelf());
		} else {
			super.unhandled(cmd);
		}

	}

	@Override
	public void onReceiveRecover(Object event) throws Exception {
		if (event instanceof CountChanged) {
			count = ((CountChanged) event).getCount();
		} else {
			unhandled(event);
		}

	}

	public String persistenceId() {
		return getSelf().path().parent().parent().name() + "-"
				+ getSelf().path().name();
	}

}
