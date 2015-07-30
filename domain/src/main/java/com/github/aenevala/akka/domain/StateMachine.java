package com.github.aenevala.akka.domain;

import com.github.aenevala.ddd.messaging.event.DomainEvent;
import scala.PartialFunction;

/**
 * Created by NevalaA on 30.7.2015.
 */
public interface StateMachine<T extends AggregateState> extends PartialFunction<DomainEvent, T> {
}
