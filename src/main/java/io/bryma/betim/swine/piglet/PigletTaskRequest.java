package io.bryma.betim.swine.piglet;

import eu.smartsocietyproject.pf.TaskDefinition;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.UUID;

public class PigletTaskRequest extends TaskRequest {

    private String request;

    public PigletTaskRequest(TaskDefinition definition, String type, String request) {
        super(definition, type);
        this.request = request;
    }

    @Override
    public String getRequest() {
        return this.request;
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
