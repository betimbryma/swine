package io.bryma.betim.swine.controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import io.bryma.betim.swine.model.Piglet;
import io.bryma.betim.swine.services.ExecutionService;
import io.bryma.betim.swine.services.PigletService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Piglet savePiglet(Piglet piglet){
        ActorRef actorRef = actorSystem.actorOf(Props.create(Pigle))
        return pigletService.savePiglet(piglet);
    }
}
