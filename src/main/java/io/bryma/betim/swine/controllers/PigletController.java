package io.bryma.betim.swine.controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.model.Piglet;
import io.bryma.betim.swine.services.ExecutionService;
import io.bryma.betim.swine.services.PigletService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/piglet")
public class PigletController {

    private final PigletService pigletService;
    private final ExecutionService executionService;
    private final ActorSystem actorSystem;

    public PigletController(ActorSystem actorSystem, PigletService pigletService, ExecutionService executionService) {
        this.actorSystem = actorSystem;
        this.pigletService = pigletService;
        this.executionService = executionService;
    }

    @PostMapping("/save")
    public Piglet savePiglet(@RequestBody Piglet piglet){
        Peer peer = new Peer();
        peer.setId("betim");
        piglet.setOwner(peer);
        return pigletService.savePiglet(piglet);
    }

    @GetMapping("/all")
    public List<Piglet> getAll(){
        Peer peer = new Peer();
        peer.setId("betim");
        return pigletService.getPiglets(peer);
    }
}
