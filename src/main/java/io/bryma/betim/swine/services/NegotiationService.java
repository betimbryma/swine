package io.bryma.betim.swine.services;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.ResidentCollective;
import eu.smartsocietyproject.pf.TaskRequest;
import io.bryma.betim.swine.DTO.NegotiationDTO;
import io.bryma.betim.swine.config.Vote;
import io.bryma.betim.swine.exceptions.VoterException;
import io.bryma.betim.swine.model.Negotiation;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.repositories.NegotiationRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NegotiationService {

    private final NegotiationRepository negotiationRepository;
    private final ActorSystem actorSystem;

    public NegotiationService(NegotiationRepository negotiationRepository, ActorSystem actorSystem) {
        this.negotiationRepository = negotiationRepository;
        this.actorSystem = actorSystem;
    }

    public void negotiate(Peer peer, NegotiationDTO negotiationDTO) throws VoterException {

        Negotiation negotiation = negotiationRepository.findById(negotiationDTO.getNegotiationID())
                .orElseThrow(() -> new VoterException("Negotiation not found."));

        Set<Member> negotiables = negotiation.getNegotiables();

        if(negotiables == null || !negotiables.contains(Member.of(peer.getPeerId(), peer.getRole())))
            throw new VoterException("Voter is not part of negotiables");

        Set<Member> agreed = negotiation.getAgreed();

        if(agreed == null) {
            agreed = new HashSet<>();
            negotiation.setAgreed(agreed);
        }

        if(negotiationDTO.getVote() == Vote.YES)
            agreed.add(Member.of(peer.getPeerId(), peer.getRole()));

        negotiationRepository.save(negotiation);

        if(agreed.size() == negotiables.size())
            notify(negotiation.getId(), ImmutableSet.copyOf(agreed));
    }

    public String createNegotiation(ResidentCollective residentCollective, TaskRequest taskRequest){

        Set<Member> peers = residentCollective.getMembers().asList().stream().collect(Collectors.toSet());
        Negotiation negotiation = new Negotiation(taskRequest.getType(), taskRequest.getDefinition().getJson(),
                peers, residentCollective.getId());
        return negotiationRepository.save(negotiation).getId();
    }

    private void notify(String id, ImmutableSet<Member> peers){
        ActorSelection actorSelection = actorSystem.actorSelection("/piglet/negotiationHandlers/"+id);
        ActorRef negotiationHandler = actorSelection.anchor();
        negotiationHandler.tell(peers, ActorRef.noSender());
    }
}
