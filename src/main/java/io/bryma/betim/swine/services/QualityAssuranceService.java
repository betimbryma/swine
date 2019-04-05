package io.bryma.betim.swine.services;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.TaskResult;
import io.bryma.betim.swine.DTO.QualityAssuranceDTO;
import io.bryma.betim.swine.exceptions.PeerException;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.model.QualityAssurance;
import io.bryma.betim.swine.repositories.QualityAssuranceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QualityAssuranceService {

    private final QualityAssuranceRepository qualityAssuranceRepository;
    private final ActorSystem actorSystem;
    @Value("${swine.url}")
    private String swineUrl;

    public QualityAssuranceService(QualityAssuranceRepository qualityAssuranceRepository, ActorSystem actorSystem) {
        this.qualityAssuranceRepository = qualityAssuranceRepository;
        this.actorSystem = actorSystem;
    }

    public void qualityAssurance(Peer peer, QualityAssuranceDTO dto) throws PeerException {

        QualityAssurance qualityAssurance = qualityAssuranceRepository.findById(dto.getQualityAssuranceId())
                .orElseThrow(() -> new PeerException("Quality Assurance Instance not found"));

        Set<QualityAssuranceDTO> qualityAssuranceDTOS = qualityAssurance.getQualityAssuranceVoters();
        try {
            QualityAssuranceDTO qualityAssuranceDTO = qualityAssuranceDTOS.stream()
                    .filter(q -> q.getQualityAssuranceId().equals(dto.getQualityAssuranceId())).collect(Collectors.toList()).get(0);
            if (qualityAssuranceDTO == null || qualityAssurance.isDone() || qualityAssuranceDTO.isVoted() ||
                    !qualityAssuranceDTO.getPeer().getId().equals(peer.getId()))
                throw new PeerException("Cannot vote");
            qualityAssuranceDTO.setScore(dto.getScore());
            qualityAssuranceDTO.setVoted(true);
            notify(qualityAssurance.getActorPath(), QualityAssuranceDTO.of(qualityAssuranceDTO));
            qualityAssuranceRepository.save(qualityAssurance);
        } catch (IndexOutOfBoundsException e){
            throw new PeerException("Quality Assurance Instance does not exist");
        }

    }

    public String createQualityAssurance(Set<Member> members, TaskRequest taskRequest, String actorPath){
        Set<QualityAssuranceDTO> qualityAssuranceDTOS =
                members.stream().map(member -> new QualityAssuranceDTO(Peer.of(member))).collect(Collectors.toSet());
        QualityAssurance qualityAssurance = new QualityAssurance(taskRequest.getRequest(),
                qualityAssuranceDTOS, actorPath);
        return qualityAssuranceRepository.save(qualityAssurance).getId();
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
