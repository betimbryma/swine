package io.bryma.betim.swine.controllers;

import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.model.Piglet;
import io.bryma.betim.swine.services.PigletService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/piglet")
public class PigletController {

    private final PigletService pigletService;

    public PigletController(PigletService pigletService) {
        this.pigletService = pigletService;
    }

    @PostMapping("/save")
    public Piglet savePiglet(@RequestBody Piglet piglet, Principal principal){
        piglet.setOwner(new Peer(principal.getName()));
        return pigletService.savePiglet(piglet);
    }

    @GetMapping("/all")
    public List<Piglet> getAll(Principal principal){
        return pigletService.getPiglets(new Peer(principal.getName()));
    }
}
