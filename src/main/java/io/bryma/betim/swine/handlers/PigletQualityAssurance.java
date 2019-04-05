package io.bryma.betim.swine.handlers;

import akka.actor.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.DTO.ResultDTO;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.*;
import eu.smartsocietyproject.pf.enummerations.State;
import eu.smartsocietyproject.smartcom.SmartComServiceRestImpl;
import io.bryma.betim.swine.handlers.qaHandler.QAExecutionHandler;
import io.bryma.betim.swine.handlers.qaHandler.QAQualityAssuranceHandler;
import io.bryma.betim.swine.piglet.PigletTaskRequest;
import io.bryma.betim.swine.piglet.PigletTaskRunner;
import io.bryma.betim.swine.services.ExecutionService;
import io.bryma.betim.swine.services.NegotiationService;
import io.bryma.betim.swine.services.QualityAssuranceService;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class PigletQualityAssurance extends AbstractActorWithTimers implements QualityAssuranceHandler{

    private final ApplicationContext context;
    private ActorRef parent;
    private QualityAssuranceService qualityAssuranceService;
    private TaskRequest taskRequest;
    private final String TICK = "TICK";
    private Duration duration;
    private NegotiationService negotiationService;
    private ExecutionService executionService;

    public static Props props(ApplicationContext context, QualityAssuranceService qualityAssuranceService,
            NegotiationService negotiationService, ExecutionService executionService, TaskRequest taskRequest, Duration duration) {
        return Props.create(PigletQualityAssurance.class, () -> new PigletQualityAssurance(context, qualityAssuranceService,
                negotiationService, executionService, taskRequest, duration));
    }

    @Override
    public void preStart(){
        this.parent = getContext().getParent();
    }

    private PigletQualityAssurance(ApplicationContext context, QualityAssuranceService qualityAssuranceService, NegotiationService negotiationService,
                                   ExecutionService executionService, TaskRequest taskRequest, Duration duration){
        this.context = context;
        this.qualityAssuranceService = qualityAssuranceService;
        this.taskRequest = taskRequest;
        this.duration = duration;
        this.negotiationService = negotiationService;
        this.executionService = executionService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TaskResult.class, taskResult -> qualityAssurance(context, taskResult))
                .match(ResultDTO.class, resultDTO -> parent.tell(resultDTO, getSelf()))
                .build();
    }

    @Override
    public void qualityAssurance(ApplicationContext context, TaskResult taskResult) {
        if(duration != null && duration.toMinutes() >= 1)
            getTimers().startSingleTimer(TICK, PoisonPill.getInstance(), duration);

        String req = "Please rate the following result: \n"+taskResult.getResult()+"\n with the original request:\n"+taskRequest.getRequest();

        TaskDefinition taskDefinition = new TaskDefinition(new ObjectMapper().valueToTree(req));
        PigletTaskRequest pigletTaskRequest = new PigletTaskRequest(taskDefinition, "qualityAssurance");

        LocalDateTime endDate = LocalDateTime.now().plusMinutes(duration.toMinutes());

        CollectiveKindRegistry kindRegistry = CollectiveKindRegistry
                .builder().register(CollectiveKind.EMPTY).build();
        MongoRunner runner;
        try {
            runner = MongoRunner.withPort(6668);
        } catch (IOException e) {
            parent.tell(State.QUALITY_ASSURANCE_FAIL, getSelf());
            return;
        }

        PeerManagerMongoProxy.Factory pmFactory
                = PeerManagerMongoProxy.factory(runner.getMongoDb());

        SmartSocietyApplicationContext smartSocietyApplicationContext= new SmartSocietyApplicationContext(kindRegistry,
                pmFactory,
                new SmartComServiceRestImpl.Factory(), context.getPaymentService());

        PeerQuery peerQuery = PeerQuery.create().withRule(
                QueryRule.create("quality-assurance")
                .withKey("role")
                .withValue(AttributeType.from("quality-assurance"))
                .withOperation(QueryOperation.equals)
        );

        Collective collective;

        try {
            collective = ApplicationBasedCollective.createFromQuery(smartSocietyApplicationContext, peerQuery);
        } catch (PeerManagerException e) {
            parent.tell(State.QUALITY_ASSURANCE_FAIL, getSelf());
            return;
        }

        Props provisioningProps = PigletProvisioning.props(smartSocietyApplicationContext,
                pigletTaskRequest, Duration.ofMinutes(0));

        Props negotiationProps = PigletNegotiation.props(smartSocietyApplicationContext,
                pigletTaskRequest, negotiationService, Duration.ofMinutes(0));

        Props executionProps = QAExecutionHandler.props(smartSocietyApplicationContext,
                qualityAssuranceService, taskRequest);

        Props qualityAssuranceProps = QAQualityAssuranceHandler.props();

        TaskFlowDefinition taskFlowDefinition
                =  TaskFlowDefinition.onDemandWithoutOpenCall(ImmutableList.of(provisioningProps),
                ImmutableList.of(negotiationProps), ImmutableList.of(executionProps), ImmutableList.of(qualityAssuranceProps)).withCollectiveForProvisioning(collective);

        ActorRef collectiveBasedTask = getContext().getSystem()
                .actorOf(CollectiveBasedTask.props(smartSocietyApplicationContext, pigletTaskRequest,
                        taskFlowDefinition));

        collectiveBasedTask.tell(State.WAITING_FOR_PROVISIONING, getSelf());

    }
}
