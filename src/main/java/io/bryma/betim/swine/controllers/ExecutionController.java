package io.bryma.betim.swine.controllers;

import eu.smartsocietyproject.peermanager.PeerManagerException;
import io.bryma.betim.swine.DTO.CBTDTO;
import io.bryma.betim.swine.DTO.ExecutionDTO;
import io.bryma.betim.swine.exceptions.PeerException;
import io.bryma.betim.swine.exceptions.PigletNotFoundException;
import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.model.TaskResult;
import io.bryma.betim.swine.services.ExecutionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        Execution execution;
        try {
            execution = this.executionService.saveExecution(cbt, principal.getName());
        } catch (PigletNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(execution);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getCBT(@PathVariable Long id, Principal principal){
        ExecutionDTO executionDTO;
        try {
            executionDTO = this.executionService.getExecution(principal.getName(), id);
        } catch (PigletNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(executionDTO);
    }

    @PostMapping("execute")
    public ResponseEntity<?> saveExecution(@RequestBody ExecutionDTO executionDTO, Principal principal){

        try {
            executionService.execution(principal.getName(), executionDTO);
            return ResponseEntity.ok().build();
        } catch (PigletNotFoundException | PeerException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


}
