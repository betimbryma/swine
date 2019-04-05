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
import jnr.a64asm.Mem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NegotiationService {

    private final NegotiationRepository negotiationRepository;
    private final ActorSystem actorSystem;
    @Value("${swine.url}")
    private String swineUrl;

    public NegotiationService(NegotiationRepository negotiationRepository, ActorSystem actorSystem) {
        this.negotiationRepository = negotiationRepository;
        this.actorSystem = actorSystem;
    }

    public void negotiate(Peer peer, NegotiationDTO negotiationDTO) throws PeerException {

        Negotiation negotiation = negotiationRepository.findById(negotiationDTO.getNegotiationID())
                .orElseThrow(() -> new PeerException("Negotiation not found."));
        Set<Peer> negotiables = negotiation.getNegotiables();

        if(!negotiables.contains(peer) || negotiation.isDone())
            throw new PeerException("Cannot negotiate");

        Set<Peer> agreed = negotiation.getAgreed();

        if(negotiationDTO.getVote() == Vote.YES) {
            agreed.add(peer);
            notify(negotiation.getActorPath(), peer);
        }

        if(agreed.size() == negotiables.size())
            negotiation.setDone(true);

        negotiationRepository.save(negotiation);

    }

    public String createNegotiation(Set<Member> members, TaskRequest taskRequest, ActorPath actorPath){

        Set<Peer> voters = members.stream().map(Peer::of).collect(Collectors.toSet());
        Negotiation negotiation = new Negotiation(taskRequest.getType(), taskRequest.getDefinition().toString(),
                voters, actorPath.toString());
        return negotiationRepository.save(negotiation).getId();
    }

    private void notify(String path, Peer peer){
        ActorSelection actorSelection = actorSystem.actorSelection(path);
        ActorRef negotiationHandler = actorSelection.anchor();
        negotiationHandler.tell(peer, ActorRef.noSender());
    }

    public String getUrl() {
        return swineUrl+"negotiation/";
    }
}
