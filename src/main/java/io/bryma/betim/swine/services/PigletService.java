package io.bryma.betim.swine.services;

import akka.actor.ActorSystem;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.model.Piglet;
import io.bryma.betim.swine.repositories.PigletRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PigletService {

    private PigletRepository pigletRepository;
    private ExecutionService executionService;
    private ActorSystem actorSystem;

    public PigletService(PigletRepository pigletRepository, ExecutionService executionService, ActorSystem actorSystem) {
        this.pigletRepository = pigletRepository;
        this.executionService = executionService;
        this.actorSystem = actorSystem;
    }

    public Piglet savePiglet(Piglet piglet){
        return pigletRepository.save(piglet);
   }

   public Piglet getPiglet(Piglet piglet){
        return pigletRepository.findById(piglet.getId()).get(); //todo update this
   }

   public List<Piglet> getPiglets(Peer peer){
        return pigletRepository.findByOwner(peer);
   }
}
