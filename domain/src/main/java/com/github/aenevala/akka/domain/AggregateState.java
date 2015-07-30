package com.github.aenevala.akka.domain;

import com.github.aenevala.ddd.messaging.event.DomainEvent;
import scala.PartialFunction;

import javax.swing.plaf.nimbus.State;

/**
 * Created by NevalaA on 30.7.2015.
 */
public interface AggregateState<T extends AggregateState> {

    T apply(DomainEvent event);

}
