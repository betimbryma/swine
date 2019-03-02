package io.bryma.betim.swine.services.Scenario1;

import eu.smartsocietyproject.pf.TaskRequest;

public class S1TaskRequest extends TaskRequest {

    public S1TaskRequest(S1TaskDefinition definition) {
        super(definition, "BingRequestTask");
    }

    @Override
    public String getRequest() {
        return getDefinition().getJson().get("question").asText();
    }

    @Override
    public S1TaskDefinition getDefinition(){
        return (S1TaskDefinition) super.getDefinition();
    }
}
