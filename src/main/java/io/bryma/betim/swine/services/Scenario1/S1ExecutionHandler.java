package io.bryma.betim.swine.services.Scenario1;

import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskResult;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ExecutionHandler;
import eu.smartsocietyproject.smartcom.SmartComServiceImpl;

import java.io.IOException;
import java.util.Properties;

public class S1ExecutionHandler implements ExecutionHandler, NotificationCallback {

    private String conversationId;
    private S1Plan s1Plan;
    private S1TaskResult s1TaskResult;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void notify(Message message) {
        s1TaskResult.setHumanResult(message.getContent());
    }

    @Override
    public TaskResult execute(ApplicationContext applicationContext, CollectiveWithPlan collectiveWithPlan) throws CBTLifecycleException {

        SmartComServiceImpl smartComService = (SmartComServiceImpl) applicationContext.getSmartCom();

        Identifier callback = smartComService.registerNotificationCallback(this);

        s1Plan = (S1Plan) collectiveWithPlan.getPlan();

        conversationId = s1Plan.getRequest().getId().toString();

        Properties properties = new Properties();
        try {
            properties.load(Scenario1.class.getClassLoader()
                    .getResourceAsStream("EmailAdapter.properties"));
            smartComService.addEmailPullAdapter(conversationId, properties);

            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            objectNode.set("question", JsonNodeFactory.instance
                    .textNode(s1Plan.getRequest().getRequest()));

            Message message = new Message.MessageBuilder()
                    .setType("ask")
                    .setSubtype("question")
                    .setReceiverId(Identifier.collective(collectiveWithPlan.getCollective().getId()))
                    .setSenderId(Identifier.component("RQA"))
                    .setConversationId(conversationId)
                    .setContent(objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(objectNode))
                    .create();

            smartComService.send(message);
        } catch (IOException | CommunicationException e) {
            e.printStackTrace();
        } finally {
            smartComService.unregisterNotificationCallback(callback);
        }

        return s1TaskResult;
    }

    @Override
    public double resultQoR() {
        return s1TaskResult.QoR();
    }

    @Override
    public TaskResult getResultIfQoRGoodEnough() {
        return s1TaskResult;
    }
}
