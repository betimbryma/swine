package io.bryma.betim.swine.handlers;

import akka.actor.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.mongodb.MongoClient;
import eu.smartsocietyproject.DTO.ResultDTO;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.*;
import eu.smartsocietyproject.pf.enummerations.State;
import eu.smartsocietyproject.smartcom.SmartComServiceRestImpl;
import io.bryma.betim.swine.DTO.Death;
import io.bryma.betim.swine.config.LocalMail;
import io.bryma.betim.swine.config.LocalSmartCom;
import io.bryma.betim.swine.handlers.qaHandler.QAExecutionHandler;
import io.bryma.betim.swine.handlers.qaHandler.QAQualityAssuranceHandler;
import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.Piglet;
import io.bryma.betim.swine.piglet.PigletTaskRequest;
import io.bryma.betim.swine.piglet.PigletTaskRunner;
import io.bryma.betim.swine.services.ExecutionService;
import io.bryma.betim.swine.services.NegotiationService;
import io.bryma.betim.swine.services.PigletService;
import io.bryma.betim.swine.services.QualityAssuranceService;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class PigletQualityAssurance extends AbstractActorWithTimers implements QualityAssuranceHandler{

    private final ApplicationContext context;
    private final LocalSmartCom.Factory smartcomFactory;
    private ActorRef parent;
    private QualityAssuranceService qualityAssuranceService;
    private TaskRequest taskRequest;
    private final String TICK = "TICK";
    private Duration duration;
    private NegotiationService negotiationService;
    private ExecutionService executionService;
    private PigletService pigletService;
    private TaskResult taskResult;
    private final PeerQuery qaPeerQuery;
    private final double qor;

    public static Props props(ApplicationContext context, LocalSmartCom.Factory smartcomFactory, QualityAssuranceService qualityAssuranceService,
            NegotiationService negotiationService, ExecutionService executionService, PigletService pigletService, TaskRequest taskRequest,
                              Duration duration, PeerQuery qaPeerQuery, double qor) {
        return Props.create(PigletQualityAssurance.class, () -> new PigletQualityAssurance(context, smartcomFactory, qualityAssuranceService,
                negotiationService, executionService, pigletService, taskRequest, duration, qaPeerQuery, qor));
    }

    @Override
    public void preStart(){
        this.parent = getContext().getParent();
    }

    private PigletQualityAssurance(ApplicationContext context, LocalSmartCom.Factory smartcomFactory ,
                                   QualityAssuranceService qualityAssuranceService, NegotiationService negotiationService,
                                   ExecutionService executionService, PigletService pigletService, TaskRequest taskRequest, Duration duration,
                                   PeerQuery qaPeerQuery, double qor){
        this.context = context;
        this.qualityAssuranceService = qualityAssuranceService;
        this.taskRequest = taskRequest;
        this.duration = duration;
        this.negotiationService = negotiationService;
        this.executionService = executionService;
        this.pigletService = pigletService;
        this.smartcomFactory = smartcomFactory;
        this.qaPeerQuery = qaPeerQuery;
        this.qor = qor;
    }

    @Override
    public void qualityAssurance(ApplicationContext context, TaskResult taskResult) {
        if(duration != null && duration.toMinutes() >= 1)
            getTimers().startSingleTimer(TICK, PoisonPill.getInstance(), duration);
        this.taskResult = taskResult;
        String req = "Task Request: \n"+taskRequest.getRequest()+"\n\nResults:\n\n"
                +taskResult.getResult();

        TaskDefinition taskDefinition = new TaskDefinition(new ObjectMapper().createObjectNode());
        PigletTaskRequest pigletTaskRequest = new PigletTaskRequest(taskDefinition, "qualityAssurance", req);

        LocalDateTime endDate = LocalDateTime.now().plusMinutes(duration.toMinutes());

        CollectiveKindRegistry kindRegistry = CollectiveKindRegistry
                .builder().register(CollectiveKind.EMPTY).build();


        PeerManagerMongoProxy.Factory pmFactory
                = PeerManagerMongoProxy.factory(new MongoClient("localhost", 6666).getDatabase("smartSocietyLocalMongoDB"));

        SmartSocietyApplicationContext smartSocietyApplicationContext= new SmartSocietyApplicationContext(kindRegistry,
                pmFactory,
                smartcomFactory, context.getPaymentService());


        Collective collective;

        try {
            collective = ApplicationBasedCollective.createFromQuery(smartSocietyApplicationContext, qaPeerQuery);
        } catch (PeerManagerException e) {
            parent.tell(State.QUALITY_ASSURANCE_FAIL, getSelf());
            return;
        }

        Piglet piglet = new Piglet("Quality Assurance Handler",
                "Quality Assurance Handler Collective Based Tasks", "PigletQualityAssurance");

        piglet = pigletService.savePiglet(piglet);

        Execution execution = new Execution(LocalDateTime.now(), endDate, "swine",
                req, piglet, "Quality Assurance Handler");

        execution = executionService.saveExecution(execution);

        Props provisioningProps = PigletProvisioning.props(smartSocietyApplicationContext,
                pigletTaskRequest, Duration.ofMinutes(0));

        Props negotiationProps = PigletNegotiation.props(smartSocietyApplicationContext,
                execution.getId(), negotiationService, Duration.ofMinutes(0));

        Props executionProps = QAExecutionHandler.props(smartSocietyApplicationContext,
                qualityAssuranceService, execution.getId());

        Props qualityAssuranceProps = QAQualityAssuranceHandler.props();

        TaskFlowDefinition taskFlowDefinition
                =  TaskFlowDefinition.onDemandWithoutOpenCall(ImmutableList.of(provisioningProps),
                ImmutableList.of(negotiationProps), ImmutableList.of(executionProps),
                ImmutableList.of(qualityAssuranceProps)).withCollectiveForProvisioning(collective);

        ActorRef collectiveBasedTask = getContext()
                .actorOf(CollectiveBasedTask.props(smartSocietyApplicationContext, pigletTaskRequest,
                        taskFlowDefinition));

        collectiveBasedTask.tell(State.WAITING_FOR_PROVISIONING, getSelf());

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TaskResult.class, taskResult ->
                        qualityAssurance(context, taskResult))
                .match(ResultDTO.class,
                        resultDTO -> {
                                getSender().tell(PoisonPill.getInstance(), getSelf());
                                parent.tell(new ResultDTO(resultDTO.getQor(), this.taskResult.getResult()), getSelf());
                })
                .match(Death.class, death -> this.parent.tell(State.QUALITY_ASSURANCE_FAIL, getSelf()))
                .match(State.class, state -> {
                    switch (state){
                        case PROV_FAIL:
                        case NEG_FAIL:
                        case EXEC_FAIL:
                        case QUALITY_ASSURANCE_FAIL:
                            parent.tell(State.QUALITY_ASSURANCE_FAIL, getSelf());
                            break;
                        default:
                            break;
                    }
                })
                .build();
    }
}
