package io.bryma.betim.swine.handler;

import akka.actor.AbstractActor;
import akka.actor.Props;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;

import java.util.Optional;

public class PorivisoningHandlerTest extends AbstractActor implements ProvisioningHandler {

  public static Props props(){
    return Props.create(PorivisoningHandlerTest.class, PorivisoningHandlerTest::new);
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
            .match(Collective.class,
                    collective -> context().parent().tell(collective.toApplicationBasedCollective(), getSelf()))
            .build();
  }

  @Override
  public void provision(ApplicationContext context, TaskRequest t, Optional<Collective> inputCollective) throws CBTLifecycleException {

  }
}
