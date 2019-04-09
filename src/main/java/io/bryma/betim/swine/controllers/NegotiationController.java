package io.bryma.betim.swine.controllers;

import eu.smartsocietyproject.peermanager.PeerManagerException;
import io.bryma.betim.swine.DTO.NegotiationDTO;
import io.bryma.betim.swine.exceptions.NegotiationException;
import io.bryma.betim.swine.model.Negotiable;
import io.bryma.betim.swine.model.Negotiation;
import io.bryma.betim.swine.services.NegotiationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/negotiation/")
public class NegotiationController {

    private NegotiationService negotiationService;

    public NegotiationController(NegotiationService negotiationService) {
        this.negotiationService = negotiationService;
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getNegotiation(@PathVariable Long id, Principal principal){

        try {
            NegotiationDTO negotiationDTO = negotiationService.getNegotiable(id, principal.getName());

            return ResponseEntity.ok(negotiationDTO);
        } catch (NegotiationException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("negotiate")
    public ResponseEntity<?> negotiate(@RequestBody Negotiable negotiable, Principal principal){

        try {
            negotiationService.negotiate(negotiable, principal.getName());
            return ResponseEntity.ok(negotiable);
        } catch (NegotiationException | PeerManagerException e) {
            return ResponseEntity.notFound().build();
        }

    }


}
