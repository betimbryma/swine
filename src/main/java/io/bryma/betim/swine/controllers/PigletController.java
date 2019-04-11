package io.bryma.betim.swine.controllers;

import io.bryma.betim.swine.DTO.PigletDTO;
import io.bryma.betim.swine.exceptions.PigletNotFoundException;
import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.model.Piglet;
import io.bryma.betim.swine.services.ExecutionService;
import io.bryma.betim.swine.services.PigletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/piglet")
public class PigletController {

    private final PigletService pigletService;
    private final ExecutionService executionService;

    public PigletController(PigletService pigletService,
                            ExecutionService executionService) {
        this.pigletService = pigletService;
        this.executionService = executionService;
    }

    @PostMapping("/save")
    public Piglet savePiglet(@RequestBody Piglet piglet, Principal principal){
        piglet.setOwner(principal.getName());
        return pigletService.savePiglet(piglet);
    }

    @GetMapping("/all")
    public List<Piglet> getAll(Principal principal){
        return pigletService.getPiglets(principal.getName());
    }

    @GetMapping("/cbt/{id}")
    public ResponseEntity<?> getCbts(@PathVariable Long id, Principal principal){
        try {

            Piglet piglet = pigletService.getPiglet(id, principal.getName());


            List<Execution> cbts = executionService.getExecutions(piglet);

            return ResponseEntity.ok(new PigletDTO(piglet, cbts));
        } catch (PigletNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPiglet(@PathVariable Long id, Principal principal){
        try {
            Piglet piglet = pigletService.getPiglet(id, principal.getName());
            return ResponseEntity.ok(piglet);
        } catch (PigletNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
