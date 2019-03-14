package io.bryma.betim.swine.services;

import akka.actor.ActorSystem;
import at.ac.tuwien.dsg.smartcom.SmartCom;
import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.smartcom.SmartComServiceRestImpl;
import io.bryma.betim.swine.exceptions.PigletNotFoundException;
import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.piglet.PigletTaskRequest;
import io.bryma.betim.swine.piglet.PigletTaskRunner;
import io.bryma.betim.swine.repositories.ExecutionRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExecutionService {

    private ActorSystem actorSystem;
    private ExecutionRepository executionRepository;
    private NegotiationService negotiationService;
    private SmartComServiceRestImpl smartCom;
    private PeerManager.Factory pmFactory;

    public ExecutionService(ActorSystem actorSystem, ExecutionRepository executionRepository,
                            NegotiationService negotiationService, SmartComServiceRestImpl smartCom, PeerManager.Factory pmFactory) {
        this.actorSystem = actorSystem;
        this.executionRepository = executionRepository;
        this.negotiationService = negotiationService;
        this.smartCom = smartCom;
        this.pmFactory = pmFactory;
    }

    public Execution getExecution(Peer peer, String executionId) throws PigletNotFoundException {
        Execution execution = executionRepository.findById(executionId).orElseThrow(() -> new PigletNotFoundException("Piglet not found"));
        Set<Member> peers = execution.getExecutors();

        if(peers.contains(Member.of(peer.getPeerId(), peer.getRole())))
            return execution;
        throw new PigletNotFoundException("Peer not authorized to contribute to the execution");
    }

    public Execution saveExecution(Execution execution){

        execution = executionRepository.save(execution);
        TaskDefinition taskDefinition = new TaskDefinition(execution.getJson());
        PigletTaskRequest taskRequest = new PigletTaskRequest(taskDefinition, execution.getType());
        CollectiveKindRegistry kindRegistry = CollectiveKindRegistry
                .builder().register(CollectiveKind.EMPTY).build();
        /*SmartSocietyApplicationContext context =
                new SmartSocietyApplicationContext(kindRegistry, pmFactory, smartCom)
        actorSystem.actorOf(PigletTaskRunner.props(taskRequest)) */
        return null; //TODO

    }

    public String createExecution(ResidentCollective residentCollective, PigletTaskRequest taskRequest){


        Set<Member> peers = residentCollective.getMembers().asList().stream().collect(Collectors.toSet());
       // Execution execution = new Execution(taskRequest, residentCollective.getId(), peers);
        //return executionRepository.save(execution).getId();
        return null; //TODO
    }
}
