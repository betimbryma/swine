package io.bryma.betim.swine.handler;

import akka.actor.AbstractActor;
import akka.actor.Props;
import eu.smartsocietyproject.DTO.ResultDTO;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.TaskResult;
import eu.smartsocietyproject.pf.cbthandlers.QualityAssuranceHandler;

public class QualityAssuranceHandlerTest extends AbstractActor implements QualityAssuranceHandler {

  public static Props props(){
    return Props.create(QualityAssuranceHandlerTest.class, QualityAssuranceHandlerTest::new);
  }


  @Override
  public Receive createReceive() {
    return receiveBuilder()
            .match(TaskResult.class, t -> getContext().getParent().tell(new ResultDTO(0, null), getSelf()))
            .build();
  }

  @Override
  public void qualityAssurance(ApplicationContext context, TaskResult taskResult) {

  }
}
