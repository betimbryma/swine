package io.bryma.betim.swine.services.Scenario1;

import at.ac.tuwien.dsg.smartcom.model.Identifier;
import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.pf.TaskDefinition;

public class S1TaskDefinition extends TaskDefinition {

    private Identifier sender;

    public S1TaskDefinition(JsonNode json, Identifier sender) {
        super(json);
        this.sender = sender;
    }

    public Identifier getSender() {
        return sender;
    }
}