package io.bryma.betim.swine.services;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.TaskRequest;
import io.bryma.betim.swine.DTO.QualityAssuranceDTO;
import io.bryma.betim.swine.exceptions.PeerException;
import io.bryma.betim.swine.exceptions.QualityAssuranceException;
import io.bryma.betim.swine.model.*;
import io.bryma.betim.swine.repositories.ExecutionRepository;
import io.bryma.betim.swine.repositories.QualityAssuranceInstanceRepository;
import io.bryma.betim.swine.repositories.QualityAssuranceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QualityAssuranceService {

    private final QualityAssuranceRepository qualityAssuranceRepository;
    private final ExecutionRepository executionRepository;
    private final QualityAssuranceInstanceRepository qaiRepository;
    private final ActorSystem actorSystem;
    @Value("${swine.url}")
    private String swineUrl;

    public QualityAssuranceService(QualityAssuranceRepository qualityAssuranceRepository,
                                   ExecutionRepository executionRepository, ActorSystem actorSystem,
                                   QualityAssuranceInstanceRepository qaiRepository) {
        this.qualityAssuranceRepository = qualityAssuranceRepository;
        this.actorSystem = actorSystem;
        this.qaiRepository = qaiRepository;
        this.executionRepository = executionRepository;
    }

    public QualityAssuranceDTO getQualityAssurance(String peer, Long qaID) throws PeerException, QualityAssuranceException {

        QualityAssurance qualityAssurance = qualityAssuranceRepository.findById(qaID)
                .orElseThrow(() -> new QualityAssuranceException("Quality Assurance not found."));

        QualityAssuranceInstance qa = qaiRepository.getByQualityAssuranceAndPeer(qualityAssurance, peer)
                .orElseThrow(() -> new QualityAssuranceException("Quality Assurance instance not found"));

        return new QualityAssuranceDTO(qa.getId(), qualityAssurance.getExecution_qa().getRequest(),
                qualityAssurance.getExecution_qa().getTaskResults()
                        .stream().map(TaskResult::getResult).collect(Collectors.toList()));


    }

    public Long createQualityAssurance(Set<Member> members, Long executionId, String actorPath) throws QualityAssuranceException {

        Execution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new QualityAssuranceException(
                        "Quality Assurance task not found"
                ));

        QualityAssurance qualityAssurance = qualityAssuranceRepository.save(
                new QualityAssurance(execution, actorPath)
        );

        members.stream().forEach(member -> {
            QualityAssuranceInstance instance = new QualityAssuranceInstance(member.getPeerId(), qualityAssurance);
            qaiRepository.save(instance);
        });

        return qualityAssurance.getId();

    }

    private void notify(String path, QualityAssuranceDTO.ImmutableQualityAssuranceDTO qualityAssuranceDTO){
        ActorSelection actorSelection = actorSystem.actorSelection(path);
        ActorRef qualityAssuranceHandler = actorSelection.anchor();
        qualityAssuranceHandler.tell(qualityAssuranceDTO, ActorRef.noSender());
    }

    public String getUrl() {
        return swineUrl+"qualityAssurance/";
    }

}
