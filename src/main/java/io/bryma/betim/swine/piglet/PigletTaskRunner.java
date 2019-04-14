package io.bryma.betim.swine.piglet;

import akka.actor.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.DTO.ResultDTO;
import eu.smartsocietyproject.payment.smartcontracts.ICollectiveBasedTaskContract;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.enummerations.State;
import io.bryma.betim.swine.config.LocalSmartCom;
import io.bryma.betim.swine.config.PigletState;
import io.bryma.betim.swine.exceptions.PigletNotFoundException;
import io.bryma.betim.swine.handlers.*;
import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.Piglet;
import io.bryma.betim.swine.services.ExecutionService;
import io.bryma.betim.swine.services.NegotiationService;
import io.bryma.betim.swine.services.PigletService;
import io.bryma.betim.swine.services.QualityAssuranceService;

import java.time.Duration;

public class PigletTaskRunner extends AbstractActorWithTimers implements TaskRunner {

    private final PigletTaskRequest pigletTaskRequest;
    private final SmartSocietyApplicationContext smartSocietyApplicationContext;
    private final LocalSmartCom.Factory smartcomFactory;
    private final PeerQuery peerQuery;
    private final PeerQuery qaPeerQuery;
    private final NegotiationService negotiationService;
    private final ExecutionService executionService;
    private final PigletService pigletService;
    private final QualityAssuranceService qualityAssuranceService;
    private final double qor;
    private ActorRef parent;
    private final String START_TICK = "START_TICK";
    private final String END_TICK = "END_TICK";
    private final Long executionId;
    private boolean openCall;
    private Duration provisionDuration;
    private Duration compositionDuration;
    private Duration negotiationDuration;
    private Duration executionDuration;
    private Duration qualityAssuranceDuration;
    private ICollectiveBasedTaskContract collectiveBasedTaskContract;

    private static final class PigletTick{}

    public static Props props(PigletTaskRequest pigletTaskRequest, SmartSocietyApplicationContext smartSocietyApplicationContext, LocalSmartCom.Factory smartcomFactory,
                              PeerQuery peerQuery, PeerQuery qaPeerQuery, NegotiationService negotiationService, ExecutionService executionService,
                              QualityAssuranceService qualityAssuranceService, PigletService pigletService, ICollectiveBasedTaskContract collectiveBasedTaskContract,
                              Duration start, Duration end, Long executionId, boolean openCall, Duration provision,
                              Duration composition, Duration negotiation, Duration execution, Duration qualityAssurance, double qor){
        return Props.create(PigletTaskRunner.class, () -> new PigletTaskRunner(pigletTaskRequest, smartSocietyApplicationContext, smartcomFactory,
                peerQuery, qaPeerQuery, negotiationService, executionService, qualityAssuranceService, pigletService, collectiveBasedTaskContract,
                start, end, executionId, openCall, provision, composition, negotiation, execution, qualityAssurance, qor));
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        this.parent = getContext().getParent();
    }

    private PigletTaskRunner(PigletTaskRequest pigletTaskRequest, SmartSocietyApplicationContext smartSocietyApplicationContext, LocalSmartCom.Factory smartcomFactory,
                             PeerQuery peerQuery, PeerQuery qaPeerQuery, NegotiationService negotiationService, ExecutionService executionService,
                             QualityAssuranceService qualityAssuranceService, PigletService pigletService, ICollectiveBasedTaskContract collectiveBasedTaskContract, Duration start, Duration end, Long executionId, boolean openCall, Duration provision,
                             Duration composition, Duration negotiation, Duration execution, Duration qualityAssurance, double qor) {
        if(end != null && end.toMinutes() > 0)
            getTimers().startSingleTimer(END_TICK, PoisonPill.getInstance(), end);
        this.pigletTaskRequest = pigletTaskRequest;
        this.smartSocietyApplicationContext = smartSocietyApplicationContext;
        this.peerQuery = peerQuery;
        this.negotiationService = negotiationService;
        this.executionService = executionService;
        this.qualityAssuranceService = qualityAssuranceService;
        this.executionId = executionId;
        this.openCall = openCall;
        this.provisionDuration = provision;
        this.compositionDuration = composition;
        this.negotiationDuration = negotiation;
        this.executionDuration = execution;
        this.qualityAssuranceDuration = qualityAssurance;
        this.pigletService = pigletService;
        this.qaPeerQuery = qaPeerQuery;
        this.smartcomFactory = smartcomFactory;
        this.qor = qor;
        if(start != null && start.toMinutes() > 0)
            getTimers().startSingleTimer(START_TICK, new PigletTick(), start);
        else
            start();
    }

    private void start(){

        try {

            Collective collective = ApplicationBasedCollective
                    .createFromQuery(smartSocietyApplicationContext, peerQuery);

            Props provisioningProps = PigletProvisioning.props(smartSocietyApplicationContext,
                    pigletTaskRequest, provisionDuration);

            Props compositionProps = PigletComposition.props(smartSocietyApplicationContext,
                    pigletTaskRequest, compositionDuration);

            Props negotiationProps = PigletNegotiation.props(smartSocietyApplicationContext,
                    executionId, negotiationService, negotiationDuration);

            Props executionProps = PigletExecution.props(smartSocietyApplicationContext,
                    executionService, executionId, executionDuration);

            Props qualityAssuranceProps = PigletQualityAssurance.props(smartSocietyApplicationContext, smartcomFactory,
                    qualityAssuranceService, negotiationService, executionService, pigletService, pigletTaskRequest,
                    qualityAssuranceDuration, qaPeerQuery, qor);

            TaskFlowDefinition taskFlowDefinition
                    = this.openCall ? TaskFlowDefinition.onDemandWithOpenCall(
                    ImmutableList.of(provisioningProps), ImmutableList.of(compositionProps), ImmutableList.of(negotiationProps),
                    ImmutableList.of(executionProps), ImmutableList.of(qualityAssuranceProps)
            ).withCollectiveForProvisioning(collective) : TaskFlowDefinition.onDemandWithoutOpenCall(ImmutableList.of(provisioningProps),
                    ImmutableList.of(negotiationProps), ImmutableList.of(executionProps), ImmutableList.of(qualityAssuranceProps)).withCollectiveForProvisioning(collective);

            ActorRef collectiveBasedTask = getContext()
                    .actorOf(CollectiveBasedTask.props(smartSocietyApplicationContext, pigletTaskRequest,
                            taskFlowDefinition));

            collectiveBasedTask.tell(State.WAITING_FOR_PROVISIONING, getSelf());

        } catch (PeerManagerException e) {
            parent.tell(e, getSelf());
            e.printStackTrace();
        }

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PigletTick.class,
                        p -> start())
                .match(State.class, state -> {

                  Execution execution =
                          getExecution();

                    switch (state){
                        case PROVISIONING:
                            execution.setProvisioning(true);
                            updateState(PigletState.PROVISIONING, execution);
                            break;
                        case NEGOTIATION:
                            execution.setNegotiation(true);
                            updateState(PigletState.NEGOTIATING, execution);
                            break;
                        case COMPOSITION:
                            execution.setComposition(true);
                            updateState(PigletState.COMPOSITION, execution);
                            break;
                        case EXECUTION:
                            execution.setExecution(true);
                            updateState(PigletState.EXECUTION, execution);
                            break;
                        case QUALITY_ASSURANCE:
                            execution.setQualityAssurance(true);
                            updateState(PigletState.QUALITY_ASSURANCE, execution);
                            break;
                        case PROV_FAIL:
                        case COMP_FAIL:
                        case NEG_FAIL:
                        case EXEC_FAIL:
                        case QUALITY_ASSURANCE_FAIL:
                            updateState(PigletState.FAILED, execution);
                            break;
                        default:
                            break;
                    }
                })
                .match(ResultDTO.class, resultDTO -> updateState(resultDTO.getQor() >= this.qor ? PigletState.FINISHED : PigletState.FAILED, getExecution()))
                .build();
    }

    @Override
    public JsonNode getStateDescription() {
        return this.pigletTaskRequest.getDefinition().getJson();
    }

    private void updateState(PigletState state, Execution execution){
            execution.setState(state);
            executionService.saveExecution(execution);
    }

    private Execution getExecution() throws PigletNotFoundException {
      return executionService.getExecution(this.executionId);
    }
}
