package io.bryma.betim.swine.handler;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.DTO.ExecutionHandlerDTO;
import eu.smartsocietyproject.DTO.NegotiationHandlerDTO;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;

public class NegotiationHandlerTest extends AbstractActor implements NegotiationHandler {

  public static Props props(){
    return Props.create(NegotiationHandlerTest.class, NegotiationHandlerTest::new);
  }
  @Override
  public Receive createReceive() {
    return receiveBuilder()
            .match(NegotiationHandlerDTO.class,
                    negotiationHandlerDTO -> getContext().parent().tell(new ExecutionHandlerDTO(CollectiveWithPlan.of(null, null)), getSelf()))
            .build();
  }

  @Override
  public void negotiate(ApplicationContext context, ImmutableList<CollectiveWithPlan> negotiables) throws CBTLifecycleException {

  }
}
