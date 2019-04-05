package io.bryma.betim.swine.handlers.qaHandler;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ExecutionHandler;
import eu.smartsocietyproject.pf.enummerations.State;
import io.bryma.betim.swine.DTO.QualityAssuranceDTO;
import io.bryma.betim.swine.piglet.PigletTaskResult;
import io.bryma.betim.swine.services.QualityAssuranceService;

import java.util.*;

public class QAExecutionHandler extends AbstractActor implements ExecutionHandler {

    private TaskRequest taskRequest;
    private ActorRef parent;
    private final ApplicationContext context;
    private final QualityAssuranceService qualityAssuranceService;
    private Set<Member> peers = new HashSet<>();
    private Set<QualityAssuranceDTO.ImmutableQualityAssuranceDTO> votes = new HashSet<>();

    @Override
    public void preStart() {
        this.parent = getContext().getParent();
    }

    public static Props props(ApplicationContext context, QualityAssuranceService qualityAssuranceService, TaskRequest taskRequest) {
        return Props.create(QAExecutionHandler.class, () -> new QAExecutionHandler(context, qualityAssuranceService, taskRequest));
    }

    private QAExecutionHandler(ApplicationContext context, QualityAssuranceService qualityAssuranceService, TaskRequest taskRequest){
        this.context = context;
        this.qualityAssuranceService = qualityAssuranceService;
        this.taskRequest = taskRequest;
    }

    @Override
    public void execute(ApplicationContext context, CollectiveWithPlan agreed) {

        try {
            ResidentCollective residentCollective = context.getPeerManager()
                    .readCollectiveById(agreed.getCollective().getId());
            peers = residentCollective.getMembers();

            String id = qualityAssuranceService.createQualityAssurance(peers, taskRequest, getSelf().path().toString());

            String stringBuilder = "Hi, \n You have been invited to rate the results of a Collective Based Task. " +
                    "Click on the link below for more information:\n" +
                    qualityAssuranceService.getUrl() + id +
                    "\n Best regards: \n SmartSociety";

            Message message = new Message.MessageBuilder()
                    .setType("swine")
                    .setSubtype("quality-assurance")
                    .setContent(stringBuilder)
                    .setSenderId(Identifier.component("swine"))
                    .setReceiverId(Identifier.collective(residentCollective.getId()))
                    .create();

            context.getSmartCom().send(message);

        } catch (PeerManagerException | CommunicationException e) {
            parent.tell(State.EXEC_FAIL, getSelf());
        }

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(QualityAssuranceDTO.ImmutableQualityAssuranceDTO.class,
                        dto -> {
                            votes.add(dto);
                            if(votes.size() == peers.size()){
                                double qor = votes.stream().mapToInt(QualityAssuranceDTO.ImmutableQualityAssuranceDTO::getScore).sum();
                                PigletTaskResult pigletTaskResult = new PigletTaskResult();
                                pigletTaskResult.setResults(Collections.singletonList(String.valueOf(qor / votes.size())));
                                parent.tell(pigletTaskResult, getSelf());
                            }
                        })
                .match(CollectiveWithPlan.class,
                        collectiveWithPlan -> execute(context, collectiveWithPlan))
                .build();
    }
}
