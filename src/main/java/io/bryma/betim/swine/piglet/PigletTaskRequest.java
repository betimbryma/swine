package io.bryma.betim.swine.piglet;

import eu.smartsocietyproject.pf.TaskDefinition;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.UUID;

public class PigletTaskRequest extends TaskRequest {

    public PigletTaskRequest(TaskDefinition definition, String type) {
        super(definition, type);
    }

    @Override
    public String getRequest() {
        return null;
    }

    @Override
    public TaskDefinition getDefinition() {
        return super.getDefinition();
    }

    @Override
    public UUID getId() {
        return super.getId();
    }

    @Override
    public String getType() {
        return super.getType();
    }
}
