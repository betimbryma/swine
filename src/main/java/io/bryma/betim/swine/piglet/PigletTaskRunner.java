package io.bryma.betim.swine.piglet;

import akka.actor.*;
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

import java.time.Duration;

public class PigletTaskRunner extends AbstractActorWithTimers implements TaskRunner {

    private final PigletTaskRequest pigletTaskRequest;
    private final SmartSocietyApplicationContext smartSocietyApplicationContext;
    private final PeerQuery peerQuery;
    private final NegotiationService negotiationService;
    private final ExecutionService executionService;
    private final String url;
    private ActorRef parent;
    private final String START_TICK = "START_TICK";
    private final String END_TICK = "END_TICK";
    private final String executionId;
    private boolean openCall;

    private static final class PigletTick{}

    public static Props props(PigletTaskRequest pigletTaskRequest, SmartSocietyApplicationContext smartSocietyApplicationContext,
                              PeerQuery peerQuery, NegotiationService negotiationService, ExecutionService executionService, String url,
                                    Duration start, Duration end, String executionId, boolean openCall){
        return Props.create(PigletTaskRunner.class, () -> new PigletTaskRunner(pigletTaskRequest, smartSocietyApplicationContext,
                peerQuery, negotiationService, executionService, url, start, end, executionId, openCall));
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        this.parent = getContext().getParent();
    }

    private PigletTaskRunner(PigletTaskRequest pigletTaskRequest, SmartSocietyApplicationContext smartSocietyApplicationContext,
                             PeerQuery peerQuery, NegotiationService negotiationService, ExecutionService executionService, String url,
                             Duration start, Duration end, String executionId, boolean openCall) {
        this.pigletTaskRequest = pigletTaskRequest;
        this.smartSocietyApplicationContext = smartSocietyApplicationContext;
        this.peerQuery = peerQuery;
        this.negotiationService = negotiationService;
        this.url = url;
        this.executionService = executionService;
        getTimers().startSingleTimer(START_TICK, new PigletTick(), start);
        getTimers().startSingleTimer(END_TICK, PoisonPill.getInstance(), end);
        this.executionId = executionId;
        this.openCall = openCall;
    }

    private void start(){

        try {

            Collective collective = ApplicationBasedCollective
                    .createFromQuery(smartSocietyApplicationContext, peerQuery);

            Props provisioningProps = PigletProvisioning.props(smartSocietyApplicationContext, pigletTaskRequest);

            Props compositionProps = PigletComposition.props(smartSocietyApplicationContext, pigletTaskRequest,
                    negotiationService, url);

            Props negotiationProps = PigletNegotiation.props(smartSocietyApplicationContext,
                    pigletTaskRequest, negotiationService, url);

            Props executionProps = PigletExecution.props(smartSocietyApplicationContext,
                    executionService, executionId, url);

            TaskFlowDefinition taskFlowDefinition
                    = this.openCall ? TaskFlowDefinition.onDemandWithOpenCall(
                    provisioningProps, compositionProps, negotiationProps,
                    executionProps
            ).withCollectiveForProvisioning(collective) : TaskFlowDefinition.onDemandWithoutOpenCall(provisioningProps, negotiationProps,
                                                                executionProps).withCollectiveForProvisioning(collective);

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
                .match(PigletTick.class,
                        p -> start())
                .build();
    }

    @Override
    public JsonNode getStateDescription() {
        return this.pigletTaskRequest.getDefinition().getJson();
    }
}
