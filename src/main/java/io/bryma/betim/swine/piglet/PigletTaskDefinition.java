package io.bryma.betim.swine.piglet;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.pf.TaskDefinition;

import java.util.UUID;

public class PigletTaskDefinition extends TaskDefinition {
    public PigletTaskDefinition(JsonNode json) {
        super(json);
    }

    @Override
    public UUID getId() {
        return super.getId();
    }

    @Override
    public JsonNode getJson() {
        return super.getJson();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
