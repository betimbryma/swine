package io.bryma.betim.swine.controllers;

import io.bryma.betim.swine.services.QualityAssuranceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return null;
    }
}
