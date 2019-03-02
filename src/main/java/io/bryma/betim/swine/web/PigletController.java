package io.bryma.betim.swine.web;

import at.ac.tuwien.dsg.smartcom.rest.model.MessageDTO;
import io.bryma.betim.swine.services.Scenario1.Scenario1;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.CommunicationException;
import java.io.IOException;

@RestController
@RequestMapping("/api/piglet")
public class PigletController {

    private Scenario1 scenario1;

    public PigletController(Scenario1 scenario1){
        this.scenario1 = scenario1;
    }

    @PostMapping("test")
    public ResponseEntity<?> notifyPiglet(@RequestBody MessageDTO messageDTO) throws IOException, CommunicationException, at.ac.tuwien.dsg.smartcom.exception.CommunicationException, InstantiationException {
        scenario1.notify(messageDTO.create());
        //System.out.println("well..");
        //return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("test")
    public ResponseEntity<?> createNewPiglet() throws IOException, CommunicationException, at.ac.tuwien.dsg.smartcom.exception.CommunicationException, InstantiationException {
        scenario1.start();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
