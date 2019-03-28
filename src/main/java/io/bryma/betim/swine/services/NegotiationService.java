package io.bryma.betim.swine.services;

import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.TaskRequest;
import io.bryma.betim.swine.DTO.NegotiationDTO;
import io.bryma.betim.swine.config.Vote;
import io.bryma.betim.swine.exceptions.PeerException;
import io.bryma.betim.swine.model.Negotiation;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.repositories.NegotiationRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class NegotiationService {

    private final NegotiationRepository negotiationRepository;
    private final ActorSystem actorSystem;

    public NegotiationService(NegotiationRepository negotiationRepository, ActorSystem actorSystem) {
        this.negotiationRepository = negotiationRepository;
        this.actorSystem = actorSystem;
    }

    public void negotiate(Peer peer, NegotiationDTO negotiationDTO) throws PeerException {

        Negotiation negotiation = negotiationRepository.findById(negotiationDTO.getNegotiationID())
                .orElseThrow(() -> new PeerException("Negotiation not found."));
        Member voter = Member.of(peer.getPeerId(), peer.getRole());
        Set<Member> negotiables = negotiation.getNegotiables();

        if(!negotiables.contains(voter) || negotiation.isDone())
            throw new PeerException("Cannot vote");

        Set<Member> agreed = negotiation.getAgreed();

        if(negotiationDTO.getVote() == Vote.YES) {
            agreed.add(voter);
            notify(negotiation.getActorPath(), voter);
        }

        if(agreed.size() == negotiables.size())
            negotiation.setDone(true);

        negotiationRepository.save(negotiation);

    }

    public String createNegotiation(Set<Member> members, TaskRequest taskRequest, ActorPath actorPath){

        Negotiation negotiation = new Negotiation(taskRequest.getType(), taskRequest.getDefinition().getJson(),
                members, actorPath.toString());
        return negotiationRepository.save(negotiation).getId();
    }

    private void notify(String path, Member peer){
        ActorSelection actorSelection = actorSystem.actorSelection(path);
        ActorRef negotiationHandler = actorSelection.anchor();
        negotiationHandler.tell(peer, ActorRef.noSender());
    }
}
