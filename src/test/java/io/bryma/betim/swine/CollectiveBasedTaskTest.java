package io.bryma.betim.swine;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.DTO.ResultDTO;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.enummerations.State;
import io.bryma.betim.swine.handler.*;
import io.bryma.betim.swine.handlers.*;
import io.bryma.betim.swine.piglet.PigletTaskRequest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class CollectiveBasedTaskTest extends AbstractActor {

  public static Props props(){
    return Props.create(CollectiveBasedTaskTest.class, CollectiveBasedTaskTest::new);
  }


  private void run() throws PeerManagerException, IOException {


    CollectiveKindRegistry kindRegistry = CollectiveKindRegistry
            .builder().register(CollectiveKind.EMPTY).build();

    MongoRunner runner = MongoRunner.withPort(6666);

    PeerManagerMongoProxy.Factory pmFactory
            = PeerManagerMongoProxy.factory(runner.getMongoDb());

    SmartSocietyApplicationContext smartSocietyApplicationContext = new SmartSocietyApplicationContext(kindRegistry,
            pmFactory,
            new LocalSmartComTest.Factory(), null);
    Collective collective = ApplicationBasedCollective
            .createFromQuery(smartSocietyApplicationContext, PeerQuery.create().withRule(QueryRule.create("location")
                    .withValue(AttributeType.from("Vienna"))
                    .withOperation(QueryOperation.equals)));

    for(int i= 0; i<100; i++){
      TaskFlowDefinition taskFlowDefinition
              = TaskFlowDefinition.onDemandWithOpenCall(
              ImmutableList.of(PorivisoningHandlerTest.props()), ImmutableList.of(CompositionHandlerTest.props()),
              ImmutableList.of(NegotiationHandlerTest.props()),
              ImmutableList.of(ExecutionHandlerTest.props()), ImmutableList.of(QualityAssuranceHandlerTest.props())
      ).withCollectiveForProvisioning(collective);

      ActorRef collectiveBasedTask = getContext()
              .actorOf(CollectiveBasedTask.props(smartSocietyApplicationContext, new PigletTaskRequest(null, null, null),
                      taskFlowDefinition));

      collectiveBasedTask.tell(State.WAITING_FOR_PROVISIONING, getSelf());
    }




  }


  @Override
  public Receive createReceive() {
    return receiveBuilder()
            .match(ResultDTO.class,
                    resultDTO -> System.out.println(resultDTO))
            .match(State.class, state -> run())
            .build();
  }
}
