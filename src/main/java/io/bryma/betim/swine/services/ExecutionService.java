package io.bryma.betim.swine.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.ac.tuwien.dsg.smartcom.SmartCom;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.smartsocietyproject.peermanager.PeerManager;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.smartcom.SmartComServiceRestImpl;
import io.bryma.betim.swine.DTO.CBTDTO;
import io.bryma.betim.swine.config.PigletState;
import io.bryma.betim.swine.exceptions.PeerException;
import io.bryma.betim.swine.exceptions.PigletNotFoundException;
import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.piglet.PigletTaskRequest;
import io.bryma.betim.swine.piglet.PigletTaskRunner;
import io.bryma.betim.swine.repositories.ExecutionRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExecutionService {

    private ActorSystem actorSystem;
    private ExecutionRepository executionRepository;
    private NegotiationService negotiationService;
    private PeerManagerMongoProxy.Factory pmFactory;
    private SmartComServiceRestImpl.Factory smartComFactory;

    public ExecutionService(ActorSystem actorSystem, ExecutionRepository executionRepository,
                            NegotiationService negotiationService, PeerManagerMongoProxy.Factory pmFactory) {
        this.actorSystem = actorSystem;
        this.executionRepository = executionRepository;
        this.negotiationService = negotiationService;
        this.pmFactory = pmFactory;
        smartComFactory = new SmartComServiceRestImpl.Factory();
    }

    public Execution getExecution(Peer peer, String executionId) throws PigletNotFoundException {
        Execution execution = executionRepository.findById(executionId).orElseThrow(() -> new PigletNotFoundException("Piglet not found"));
        Set<Member> peers = execution.getExecutors();

        if(peers.contains(Member.of(peer.getPeerId(), peer.getRole())))
            return execution;
        throw new PigletNotFoundException("Peer not authorized to contribute to the execution");
    }

    public Execution saveExecution(CBTDTO cbt, Peer peer) throws PeerManagerException {

        PeerQuery peerQuery = PeerQuery.create();

        cbt.getQueries().forEach(node ->
            peerQuery.withRule(QueryRule.create(node.get("key").asText())
                        .withValue(AttributeType.from(node.get("value").asText()))
                        .withOperation(QueryOperation.equals))
        );

        TaskDefinition taskDefinition = new TaskDefinition(new ObjectMapper().valueToTree(cbt));
        PigletTaskRequest pigletTaskRequest = new PigletTaskRequest(taskDefinition, "swine");

        CollectiveKindRegistry kindRegistry = CollectiveKindRegistry
                .builder().register(CollectiveKind.EMPTY).build();
        SmartSocietyApplicationContext context =
                new SmartSocietyApplicationContext(kindRegistry, pmFactory, smartComFactory);


        Duration totalDuration = Duration.ofMinutes(cbt.getProvisionTimeout() + cbt.getCompositionTimeout()
            + cbt.getNegotiationTimeout() + cbt.getExecutionTimeout() + cbt.getQualityAssuranceTimeout());
        LocalDateTime endDate = cbt.getStartDate().plusMinutes(totalDuration.toMinutes());
        Execution execution = new Execution(cbt.getStartDate(), endDate, "swine", cbt.getQueries(),
            peer.getId(), cbt.getPigletId());

        execution = executionRepository.save(execution);
        Duration start = Duration.between(LocalDateTime.now(), cbt.getStartDate());

        actorSystem.actorOf(PigletTaskRunner.props(pigletTaskRequest, context, peerQuery, negotiationService, this, "url",
                start, totalDuration, execution.getId(), cbt.isOpenCall()));

        return execution;

    }

    public Execution initiateExecution(ResidentCollective residentCollective,
                                  String executionId, String path) throws PigletNotFoundException {


        Set<Member> peers = residentCollective.getMembers().asList().stream().collect(Collectors.toSet());
        Execution execution = executionRepository.findById(executionId).orElseThrow(() -> new PigletNotFoundException("Could not " +
                "find piglet instance with the gived ID"));
        execution.setExecutors(peers);
        execution.setActorPath(path);
        execution.setState(PigletState.RUNNING);
        return executionRepository.save(execution);
    }

    public Execution execution(Member peer, String result, String executionId) throws PigletNotFoundException, PeerException {
        Execution execution = executionRepository.findById(executionId).orElseThrow(() -> new PigletNotFoundException("Could not " +
                "find piglet instance with the given ID"));
        Set<Member> peers = execution.getExecutors();
        if(!peers.contains(peer))
            throw new PeerException("Peer not part of the Collective for this task.");
        execution.getPigletTaskResult().add(result);
        executionRepository.save(execution);
        if(execution.getExecutors().size() == execution.getPigletTaskResult().size())
            actorSystem.actorSelection(execution.getActorPath()).anchor().tell(PigletState.FINISHED, ActorRef.noSender());
        return execution;
    }
}
