package io.bryma.betim.swine.controllers;

import eu.smartsocietyproject.peermanager.PeerManagerException;
import io.bryma.betim.swine.DTO.QualityAssuranceDTO;
import io.bryma.betim.swine.exceptions.PeerException;
import io.bryma.betim.swine.exceptions.QualityAssuranceException;
import io.bryma.betim.swine.model.QualityAssuranceInstance;
import io.bryma.betim.swine.services.QualityAssuranceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/qualityassurance")
public class QualityAssuranceController {

    private QualityAssuranceService qualityAssuranceService;

    public QualityAssuranceController(QualityAssuranceService qualityAssuranceService) {
        this.qualityAssuranceService = qualityAssuranceService;
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getQualityAssurance(@PathVariable Long id, Principal principal){

        try {
            QualityAssuranceDTO qualityAssuranceDTO =
                    qualityAssuranceService.getQualityAssurance(principal.getName(), id);
            return ResponseEntity.ok(qualityAssuranceDTO);
        } catch (PeerException | QualityAssuranceException e) {
           return ResponseEntity.notFound().build();
        }

    }

    @PostMapping
    public ResponseEntity<?> qualityAsses(@RequestBody QualityAssuranceInstance qualityAssuranceInstance,
                                          Principal principal){
        try {
            qualityAssuranceService.qualityAsses(qualityAssuranceInstance, principal.getName());
            return ResponseEntity.ok().build();
        } catch (QualityAssuranceException | PeerManagerException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
