package io.bryma.betim.swine.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.smartsocietyproject.payment.PaymentService;
import eu.smartsocietyproject.payment.smartcontracts.ICollectiveBasedTaskContract;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;
import io.bryma.betim.swine.DTO.CBTDTO;
import io.bryma.betim.swine.config.PigletState;
import io.bryma.betim.swine.exceptions.PeerException;
import io.bryma.betim.swine.exceptions.PigletNotFoundException;
import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.piglet.PigletTaskRequest;
import io.bryma.betim.swine.piglet.PigletTaskResult;
import io.bryma.betim.swine.piglet.PigletTaskRunner;
import io.bryma.betim.swine.repositories.ExecutionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.tx.gas.ContractGasProvider;
import smartcontracts.CollectiveBasedTaskContract;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExecutionService {

    private final ActorSystem actorSystem;
    private final ExecutionRepository executionRepository;
    private final NegotiationService negotiationService;
    private final QualityAssuranceService qualityAssuranceService;
    private final SmartSocietyApplicationContext smartSocietyApplicationContext;
    private final ContractGasProvider contractGasProvider;
    @Value("${swine.url}")
    private String swineUrl;

    public ExecutionService(ActorSystem actorSystem, ExecutionRepository executionRepository, NegotiationService negotiationService, QualityAssuranceService qualityAssuranceService,
                            SmartSocietyApplicationContext smartSocietyApplicationContext, ContractGasProvider contractGasProvider) {
        this.actorSystem = actorSystem;
        this.executionRepository = executionRepository;
        this.negotiationService = negotiationService;
        this.qualityAssuranceService = qualityAssuranceService;
        this.smartSocietyApplicationContext = smartSocietyApplicationContext;
        this.contractGasProvider = contractGasProvider;
    }

    public Execution getExecution(Peer peer, String executionId) throws PigletNotFoundException {
        Execution execution = executionRepository.findById(executionId).orElseThrow(() -> new PigletNotFoundException("Piglet not found"));
        Set<Member> peers = execution.getExecutors();

        if(peers.contains(Member.of(peer.getId(), peer.getRole(), peer.getAddress())))
            return execution;
        throw new PigletNotFoundException("Peer not authorized to contribute to the execution");
    }

    public Execution saveExecution(CBTDTO cbt, String peerId) {

        PeerQuery peerQuery = PeerQuery.create();

        cbt.getQueries().forEach(node ->
            peerQuery.withRule(QueryRule.create(node.get("key").asText())
                        .withValue(AttributeType.from(node.get("value").asText()))
                        .withOperation(QueryOperation.equals))
        );

        TaskDefinition taskDefinition = new TaskDefinition(new ObjectMapper().valueToTree(cbt));
        PigletTaskRequest pigletTaskRequest = new PigletTaskRequest(taskDefinition, "swine");


        Duration totalDuration = Duration.ofMinutes(cbt.getProvisionTimeout() + cbt.getCompositionTimeout()
            + cbt.getNegotiationTimeout() + cbt.getExecutionTimeout() + cbt.getQualityAssuranceTimeout());
        LocalDateTime endDate = cbt.getStartDate().plusMinutes(totalDuration.toMinutes());
        Execution execution = new Execution(cbt.getStartDate(), endDate, "swine", cbt.getTaskRequest(),
                peerId, cbt.getPigletId());

        execution = executionRepository.save(execution);
        Duration start = Duration.between(LocalDateTime.now(), cbt.getStartDate());

        ICollectiveBasedTaskContract collectiveBasedTaskContract = smartSocietyApplicationContext.getPaymentService()
                .getDeployedContract(cbt.getSmartContractAddress(), contractGasProvider);

        actorSystem.actorOf(PigletTaskRunner.props(execution.getId(), pigletTaskRequest, smartSocietyApplicationContext, peerQuery, negotiationService, this, qualityAssuranceService,
                collectiveBasedTaskContract, start, totalDuration, execution.getId(), cbt.isOpenCall(), Duration.ofMinutes(cbt.getProvisionTimeout()), Duration.ofMinutes(cbt.getCompositionTimeout())
                        ,Duration.ofMinutes(cbt.getNegotiationTimeout()), Duration.ofMinutes(cbt.getExecutionTimeout()), Duration.ofMinutes(cbt.getQualityAssuranceTimeout())));


        return execution;

    }

    public Execution initiateExecution(ResidentCollective residentCollective,
                                  String executionId, String path) throws PigletNotFoundException {


        Set<Member> peers = new HashSet<>(residentCollective.getMembers().asList());
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
        execution.getResults().add(result);
        executionRepository.save(execution);
        if(execution.getExecutors().size() == execution.getResults().size()){
            PigletTaskResult pigletTaskResult = new PigletTaskResult();
            pigletTaskResult.setResults(execution.getResults());
            actorSystem.actorSelection(execution.getActorPath()).anchor().tell(pigletTaskResult, ActorRef.noSender());
        }

        return execution;
    }

    public String getUrl() {
        return swineUrl+"execution/";
    }
}
