package io.bryma.betim.swine.services;

import akka.actor.ActorSystem;
import io.bryma.betim.swine.exceptions.PigletNotFoundException;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.model.Piglet;
import io.bryma.betim.swine.repositories.PigletRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PigletService {

    private PigletRepository pigletRepository;
    private ActorSystem actorSystem;

    public PigletService(PigletRepository pigletRepository, ActorSystem actorSystem) {
        this.pigletRepository = pigletRepository;
        this.actorSystem = actorSystem;
    }

    public Piglet savePiglet(Piglet piglet){
        return pigletRepository.save(piglet);
   }

   public Piglet getPiglet(Long piglet, String peer) throws PigletNotFoundException {
        return pigletRepository.findById(piglet)
                .filter(piglet1 -> piglet1.getOwner().equals(peer)).orElseThrow(
                () -> new PigletNotFoundException("Piglet not found")
        );
   }

   public List<Piglet> getPiglets(String peer){
        return pigletRepository.findByOwner(peer);
   }
}
