package io.bryma.betim.swine.piglet;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.TaskResponse;
import eu.smartsocietyproject.pf.TaskRunner;

public class PigletTaskRunner implements TaskRunner {

    @Override
    public JsonNode getStateDescription() {
        return null;
    }

    @Override
    public TaskResponse call() throws Exception {
        return null;
    }
}
