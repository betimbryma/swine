package io.bryma.betim.swine.handler;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.DTO.NegotiationHandlerDTO;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;
import io.bryma.betim.swine.handlers.qaHandler.QAQualityAssuranceHandler;

public class CompositionHandlerTest extends AbstractActor implements CompositionHandler{

  public static Props props(){
    return Props.create(CompositionHandlerTest.class, CompositionHandlerTest::new);
  }

  @Override
  public void compose(ApplicationContext context, ApplicationBasedCollective provisioned, TaskRequest t) throws CBTLifecycleException {

  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
            .match(ApplicationBasedCollective.class, a -> getContext().parent().tell(new NegotiationHandlerDTO(ImmutableList.of()), getSender()))
            .build();
  }
}
