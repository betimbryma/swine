package io.bryma.betim.swine.services.Scenario1;

import at.ac.tuwien.dsg.smartcom.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.TaskResponse;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;

import java.util.concurrent.TimeUnit;

public class S1TaskRunner implements TaskRunner {

    private final S1TaskRequest request;
    private final SmartSocietyApplicationContext smartSocietyApplicationContext;

    public S1TaskRunner(S1TaskRequest request,
                        SmartSocietyApplicationContext smartSocietyApplicationContext) {
        this.request = request;
        this.smartSocietyApplicationContext = smartSocietyApplicationContext;
    }

    @Override
    public JsonNode getStateDescription() {
        return null;
    }

    @Override
    public TaskResponse call() throws Exception {
        Collective nearbyPeers = ApplicationBasedCollective
                .createFromQuery(smartSocietyApplicationContext,
                        PeerQuery.create()
                                .withRule(QueryRule.create("restaurantQA")
                                        .withValue(AttributeType.from("true"))
                                        .withOperation(QueryOperation.equals)));

        TaskFlowDefinition taskFlowDefinition = TaskFlowDefinition
                .onDemandWithOpenCall(new S1ProvisioningHandler(),
                        new S1CompositionHandler(),
                        new S1NegotiationHandler(),
                        new S1ExecutionHandler())
                .withCollectiveForProvisioning(nearbyPeers);

        CollectiveBasedTask collectiveBasedTask = smartSocietyApplicationContext
                .registerBuilderForCBTType("rqa", CBTBuilder.from(taskFlowDefinition)
                        .withTaskRequest(request)).build();

        collectiveBasedTask.start();

        TaskResult taskResult = collectiveBasedTask
                .get(3, TimeUnit.MINUTES);

        Message message = new Message.MessageBuilder()
                .setType("rqa")
                .setSubtype("answer")
                .setContent(taskResult.getResult())
                .setConversationId("RQA")
                .setReceiverId(this.request.getDefinition().getSender())
                .create();

        smartSocietyApplicationContext.getSmartCom().send(message);

        return TaskResponse.OK;
    }
}
