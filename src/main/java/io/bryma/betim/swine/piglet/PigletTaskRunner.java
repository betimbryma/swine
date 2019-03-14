package io.bryma.betim.swine.piglet;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.enummerations.State;
import io.bryma.betim.swine.handlers.PigletComposition;
import io.bryma.betim.swine.handlers.PigletExecution;
import io.bryma.betim.swine.handlers.PigletNegotiation;
import io.bryma.betim.swine.handlers.PigletProvisioning;
import io.bryma.betim.swine.services.ExecutionService;
import io.bryma.betim.swine.services.NegotiationService;

public class PigletTaskRunner extends AbstractActor implements TaskRunner {

    private final PigletTaskRequest pigletTaskRequest;
    private final SmartSocietyApplicationContext smartSocietyApplicationContext;
    private final String peerQuery;
    private NegotiationService negotiationService;
    private ExecutionService executionService;
    private String url;
    private ActorRef parent;

    public static Props props(PigletTaskRequest pigletTaskRequest, SmartSocietyApplicationContext smartSocietyApplicationContext,
                                    String peerQuery, NegotiationService negotiationService, ExecutionService executionService, String url){
        return Props.create(PigletTaskRunner.class, () -> new PigletTaskRunner(pigletTaskRequest, smartSocietyApplicationContext,
                peerQuery, negotiationService, executionService, url));
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        this.parent = getContext().getParent();
    }

    private PigletTaskRunner(PigletTaskRequest pigletTaskRequest, SmartSocietyApplicationContext smartSocietyApplicationContext,
                            String peerQuery, NegotiationService negotiationService, ExecutionService executionService, String url) {
        this.pigletTaskRequest = pigletTaskRequest;
        this.smartSocietyApplicationContext = smartSocietyApplicationContext;
        this.peerQuery = peerQuery;
        this.negotiationService = negotiationService;
        this.url = url;
        this.executionService = executionService;
    }

    private void start(){

        try {

            Collective collective = ApplicationBasedCollective
                    .createFromQuery(smartSocietyApplicationContext, PeerQuery.create()
                        .withRule(QueryRule.create(peerQuery)
                        .withValue(AttributeType.from("true"))
                        .withOperation(QueryOperation.equals))
                        );

            ActorRef provisioningActor = getContext().getSystem()
                    .actorOf(PigletProvisioning.props(smartSocietyApplicationContext, pigletTaskRequest));

            ActorRef compositionActor = getContext().getSystem()
                    .actorOf(PigletComposition.props(smartSocietyApplicationContext, pigletTaskRequest, negotiationService
                        ,url));

            ActorRef negotiationActor = getContext().getSystem()
                    .actorOf(PigletNegotiation.props(smartSocietyApplicationContext, pigletTaskRequest, negotiationService, url));

            ActorRef executionActor = getContext().getSystem()
                    .actorOf(PigletExecution.props(smartSocietyApplicationContext));

            TaskFlowDefinition taskFlowDefinition
                    = TaskFlowDefinition.onDemandWithOpenCall(
                    provisioningActor, compositionActor, negotiationActor,
                    executionActor
            ).withCollectiveForProvisioning(collective);

            ActorRef collectiveBasedTask = getContext().getSystem()
                    .actorOf(CollectiveBasedTask.props(smartSocietyApplicationContext, pigletTaskRequest,
                            taskFlowDefinition));

            collectiveBasedTask.tell(State.START, getSelf());

        } catch (PeerManagerException e) {
            parent.tell(e, getSelf());
            e.printStackTrace();
        }

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(State.class, s -> {
                    switch (s) {
                        case START:
                            start();
                            break;
                    }
                })
                .build();
    }

    @Override
    public JsonNode getStateDescription() {
        return null;
    }
}
