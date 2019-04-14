package io.bryma.betim.swine.handler;

import akka.actor.AbstractActor;
import akka.actor.Props;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ExecutionHandler;
import io.bryma.betim.swine.DTO.QualityAssuranceDTO;

public class ExecutionHandlerTest extends AbstractActor implements ExecutionHandler {

  public static Props props(){
    return Props.create(ExecutionHandlerTest.class, ExecutionHandlerTest::new);
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
            .match(CollectiveWithPlan.class,
                    collectiveWithPlan -> getContext().parent().tell(new QualityAssuranceDTO(1L, "", false), getSelf()))
            .build();
  }

  @Override
  public void execute(ApplicationContext context, CollectiveWithPlan agreed) throws CBTLifecycleException {

  }
}
