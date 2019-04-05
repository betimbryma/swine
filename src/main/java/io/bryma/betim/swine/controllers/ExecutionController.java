package io.bryma.betim.swine.controllers;

import eu.smartsocietyproject.peermanager.PeerManagerException;
import io.bryma.betim.swine.DTO.CBTDTO;
import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.services.ExecutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/execution/")
public class ExecutionController {

    private ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("save")
    public ResponseEntity<?> submitCBT(@RequestBody CBTDTO cbt, Principal principal){

        Execution execution = this.executionService.saveExecution(cbt, principal.getName());

        return ResponseEntity.ok(execution);
    }
}
