package io.bryma.betim.swine.services;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.helper.PeerIntermediary;
import io.bryma.betim.swine.DTO.MemberDTO;
import io.bryma.betim.swine.DTO.NegotiationDTO;
import io.bryma.betim.swine.config.Vote;
import io.bryma.betim.swine.exceptions.NegotiationException;
import io.bryma.betim.swine.exceptions.PeerException;
import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.Negotiable;
import io.bryma.betim.swine.model.Negotiation;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.repositories.ExecutionRepository;
import io.bryma.betim.swine.repositories.NegotiableRepository;
import io.bryma.betim.swine.repositories.NegotiationRepository;
import jnr.a64asm.Mem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NegotiationService {

    private final NegotiationRepository negotiationRepository;
    private final NegotiableRepository negotiableRepository;
    private final ExecutionRepository executionRepository;
    private final PeerService peerService;
    private final ActorSystem actorSystem;
    @Value("${swine.url}")
    private String swineUrl;

    public NegotiationService(NegotiationRepository negotiationRepository, ActorSystem actorSystem,
                              NegotiableRepository negotiableRepository, ExecutionRepository executionRepository,
                              PeerService peerService) {
        this.negotiationRepository = negotiationRepository;
        this.actorSystem = actorSystem;
        this.negotiableRepository = negotiableRepository;
        this.executionRepository = executionRepository;
        this.peerService = peerService;
    }

    public NegotiationDTO getNegotiable(Long negotiationId, String peer) throws NegotiationException {

        Negotiation negotiation = negotiationRepository.findById(negotiationId)
                .orElseThrow(() -> new NegotiationException("Negotiation not found."));

        Negotiable negotiable = negotiableRepository.getByNegotiationAndPeer(negotiation,
                peer).orElseThrow(
                        () -> new NegotiationException("Negotiation Instance not found."));
        return new NegotiationDTO(negotiation.getId(), negotiation.getExecution_negotiation().getRequest(),
                negotiable.isAgreed(), negotiable.isCompleted());

    }

    public void negotiate(Negotiable negotiable, String peer) throws NegotiationException, PeerManagerException {

        Negotiation negotiation = negotiationRepository.findById(negotiable.getId())
                .orElseThrow(() -> new NegotiationException("Negotiation not found."));

        Negotiable savedNegotiable = negotiableRepository.getByNegotiationAndPeer(negotiation,
                peer).orElseThrow(
                () -> new NegotiationException("Negotiation Instance not found."));

        PeerIntermediary member = peerService.getPeer(peer);

        if(!savedNegotiable.isCompleted()){
            savedNegotiable.setAgreed(negotiable.isAgreed());
            savedNegotiable.setCompleted(true);
            negotiableRepository.save(savedNegotiable);

            MemberDTO memberDTO = new MemberDTO(Member.of(peer, member.getRole(), member.getAddress()), negotiable.isAgreed());

            notify(savedNegotiable.getNegotiation().getActorPath(), memberDTO);
        }

    }

    public Long createNegotiation(Set<Member> members,
                                  Long executionId,
                                  ActorPath actorPath)
            throws NegotiationException {

        Set<Peer> negotiators = members.stream()
                .map(Peer::of)
                .collect(Collectors.toSet());

        Execution execution = executionRepository
                .findById(executionId)
                .orElseThrow(() ->
                        new NegotiationException
                                ("Execution instance not found"));



        Negotiation negotiation = negotiationRepository
                .save(new Negotiation
                        (execution, actorPath.toString()));
        negotiators.forEach(
                negotiator -> {
                    Negotiable negotiable = new
                            Negotiable
                            (negotiator.getId(), negotiation);
                    negotiableRepository.save(negotiable);
                }
        );

        return negotiation.getId();

    }

    private void notify(String path, MemberDTO dto){
        ActorSelection actorSelection = actorSystem.actorSelection(path);
        actorSelection.tell(dto, ActorRef.noSender());
    }

    public String getUrl() {
        return swineUrl+"api/negotiation/";
    }

}
