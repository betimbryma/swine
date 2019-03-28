package io.bryma.betim.swine.controllers;

import io.bryma.betim.swine.DTO.CBTDTO;
import io.bryma.betim.swine.model.Execution;
import io.bryma.betim.swine.services.ExecutionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/execution/")
public class ExecutionController {

    private ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping()
    public void submitCBT(@RequestBody CBTDTO cbt){

        //this.executionService.saveExecution(cbt);

    }
}
