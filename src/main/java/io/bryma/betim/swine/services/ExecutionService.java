package io.bryma.betim.swine.services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.smartsocietyproject.payment.PaymentService;
import eu.smartsocietyproject.payment.smartcontracts.ICollectiveBasedTaskContract;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.enummerations.State;
import io.bryma.betim.swine.DTO.CBTDTO;
import io.bryma.betim.swine.DTO.ExecutionDTO;
import io.bryma.betim.swine.DTO.TaskResultDTO;
import io.bryma.betim.swine.config.LocalMail;
import io.bryma.betim.swine.config.LocalSmartCom;
import io.bryma.betim.swine.config.PigletState;
import io.bryma.betim.swine.exceptions.PeerException;
import io.bryma.betim.swine.exceptions.PigletNotFoundException;
import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.model.Piglet;
import io.bryma.betim.swine.model.TaskResult;
import io.bryma.betim.swine.piglet.PigletTaskRequest;
import io.bryma.betim.swine.piglet.PigletTaskResult;
import io.bryma.betim.swine.piglet.PigletTaskRunner;
import io.bryma.betim.swine.repositories.ExecutionRepository;
import io.bryma.betim.swine.repositories.PigletRepository;
import io.bryma.betim.swine.repositories.TaskResultRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.tx.gas.ContractGasProvider;
import smartcontracts.CollectiveBasedTaskContract;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExecutionService {

    private final ActorSystem actorSystem;
    private final PigletService pigletService;
    private final ExecutionRepository executionRepository;
    private final TaskResultRepository taskResultRepository;
    private final PigletRepository pigletRepository;
    private final NegotiationService negotiationService;
    private final QualityAssuranceService qualityAssuranceService;
    private final SmartSocietyApplicationContext smartSocietyApplicationContext;
    private final LocalSmartCom.Factory smartcomFactory;
    @Value("${swine.url}")
    private String swineUrl;

    public ExecutionService(ActorSystem actorSystem, PigletService pigletService, ExecutionRepository executionRepository,
                            NegotiationService negotiationService, QualityAssuranceService qualityAssuranceService,
                            SmartSocietyApplicationContext smartSocietyApplicationContext, PigletRepository pigletRepository,
                            TaskResultRepository taskResultRepository, LocalSmartCom.Factory smartcomFactory) {
        this.pigletService = pigletService;
        this.actorSystem = actorSystem;
        this.executionRepository = executionRepository;
        this.negotiationService = negotiationService;
        this.qualityAssuranceService = qualityAssuranceService;
        this.smartSocietyApplicationContext = smartSocietyApplicationContext;
        this.taskResultRepository = taskResultRepository;
        this.pigletRepository = pigletRepository;
        this.smartcomFactory = smartcomFactory;
    }

    public ExecutionDTO getExecution(String peer, Long executionId) throws PigletNotFoundException {

        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(
                        () -> new PigletNotFoundException("Execution CBT not found")
                );

        TaskResult taskResult = taskResultRepository.getByExecutionAndPeer(execution, peer)
                .orElseThrow(
                        () -> new PigletNotFoundException("Execution instance not found"));
        return new ExecutionDTO(taskResult.getId(), taskResult.getResult(), execution.getRequest(), taskResult.isDone());

    }

    public Execution getExecution(Long executionId) throws PigletNotFoundException {

        return executionRepository.findById(executionId)
                .orElseThrow(
                        () -> new PigletNotFoundException("Execution CBT not found")
                );

    }

    public Execution saveExecution(CBTDTO cbt, String peerId) throws PigletNotFoundException {

        Piglet piglet = pigletRepository.findById(cbt.getPigletId())
                .orElseThrow(
                        () -> new PigletNotFoundException("Piglet not found.")
                );

        PeerQuery peerQuery = PeerQuery.create();

        cbt.getQueries().forEach(node ->
            peerQuery.withRule(QueryRule.create(node.get("key").asText())
                        .withValue(AttributeType.from(node.get("value").asText()))
                        .withOperation(QueryOperation.equals))
        );

      PeerQuery qaPeerQuery = PeerQuery.create();

      cbt.getQaQueries().forEach(node ->
              qaPeerQuery.withRule(QueryRule.create(node.get("key").asText())
                      .withValue(AttributeType.from(node.get("value").asText()))
                      .withOperation(QueryOperation.equals))
      );



      TaskDefinition taskDefinition = new TaskDefinition(new ObjectMapper().valueToTree(cbt));
        PigletTaskRequest pigletTaskRequest = new PigletTaskRequest(taskDefinition, "swine", cbt.getTaskRequest());



        LocalDateTime startDate = LocalDateTime.now().plusMinutes(cbt.getStart());
        LocalDateTime endDate = startDate.plusMinutes(cbt.getProvisionTimeout() + cbt.getStart() + cbt.getCompositionTimeout()
          + cbt.getNegotiationTimeout() + cbt.getExecutionTimeout() + cbt.getQualityAssuranceTimeout());
        Execution execution = new Execution(startDate, endDate, "swine", cbt.getTaskRequest(),
                piglet, peerId);
        execution.setName(cbt.getName());
        execution.setTaskRequest(cbt.getTaskRequest());
        execution = executionRepository.save(execution);
        Duration start = Duration.ofMinutes(cbt.getStart());

        ActorRef actorRef = actorSystem.actorOf(PigletTaskRunner.props(pigletTaskRequest, smartSocietyApplicationContext, smartcomFactory, peerQuery, qaPeerQuery, negotiationService, this, qualityAssuranceService,
                pigletService, null, start, null, execution.getId(), cbt.isOpenCall(), Duration.ofMinutes(cbt.getProvisionTimeout()), Duration.ofMinutes(cbt.getCompositionTimeout())
                        ,Duration.ofMinutes(cbt.getNegotiationTimeout()), Duration.ofMinutes(cbt.getExecutionTimeout()), Duration.ofMinutes(cbt.getQualityAssuranceTimeout()), cbt.getQor()));

        execution.setTaskRunnerPath(actorRef.path().toString());

        return executionRepository.save(execution);

    }

    public Execution saveExecution(Execution execution){
        return executionRepository.save(execution);
    }

    public Execution initiateExecution(ResidentCollective residentCollective,
                                       Long executionId, String path) throws PigletNotFoundException {

        Set<Member> peers = new HashSet<>(residentCollective.getMembers().asList());
        Execution execution = executionRepository.findById(executionId).orElseThrow(() -> new PigletNotFoundException("Could not " +
                "find execution instance with the given ID"));
        peers.stream().forEach(peer -> {
            TaskResult taskResult = new TaskResult(peer.getPeerId(), execution);
            taskResultRepository.save(taskResult);
        });
        execution.setActorPath(path);
        execution.setState(PigletState.EXECUTION);
        return executionRepository.save(execution);

    }

    public void execution(String peer, ExecutionDTO executionDTO) throws PigletNotFoundException {

        TaskResult taskResult = taskResultRepository.findById(executionDTO.getExecutionId()).orElseThrow(
                () -> new PigletNotFoundException("Execution instance not found.")
        );
        if(!taskResult.isDone()){
            taskResult.setResult(executionDTO.getResult());
            taskResult.setDone(true);
            taskResultRepository.save(taskResult);
            Execution execution = taskResult.getExecution();
            TaskResultDTO taskResultDTO = new TaskResultDTO(peer, executionDTO.getResult());
            actorSystem.actorSelection(
                    execution.getActorPath()).tell(taskResultDTO, ActorRef.noSender());
        }


    }

    public String getUrl() {
        return swineUrl+"execution/";
    }


    public List<Execution> getExecutions(Piglet piglet) {
        return executionRepository.getAllByPiglet(piglet)
                .orElse(Collections.emptyList());
    }

  public void stopExecution(ExecutionDTO executionDTO, String peer) throws PigletNotFoundException {
    Execution execution =
            executionRepository
                    .getExecutionByIdAndPeer(executionDTO.getExecutionId(), peer).orElseThrow(
                    () -> new PigletNotFoundException("Could not find execution")
            );
    execution.setState(PigletState.CANCELLED);
    executionRepository.save(execution);
    actorSystem.actorSelection(
            execution.getTaskRunnerPath()).tell(PoisonPill.getInstance(), ActorRef.noSender());
  }
}
