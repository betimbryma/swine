package io.bryma.betim.swine.piglet;

import at.ac.tuwien.dsg.smartcom.callback.NotificationCallback;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import at.ac.tuwien.dsg.smartcom.rest.model.NotificationDTO;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.TaskResult;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ExecutionHandler;
import eu.smartsocietyproject.smartcom.SmartComServiceRestImpl;
import io.bryma.betim.swine.config.SwineConstants;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

public class PigletExecution extends NotificationDTO implements ExecutionHandler, NotificationCallback {

    private String conversationId;
    private PigletPlan plan;
    private PigletTaskResult taskResult = new PigletTaskResult(null); //TODO

    @PostConstruct
    public void init(){
        super.setUrl("localhost:9080/api/swine"+Thread.currentThread().getId());
    }


    @Override
    public TaskResult execute(ApplicationContext context, CollectiveWithPlan agreed) throws CBTLifecycleException {

        this.plan = (PigletPlan) agreed.getPlan();
        this.conversationId = plan.getTaskRequest().getId().toString();

        SmartComServiceRestImpl smartComServiceRest = (SmartComServiceRestImpl)
                context.getSmartCom();

        Identifier identifier = smartComServiceRest.registerNotificationCallback(this);


        return null;
    }

    @Override
    public double resultQoR() {
        return 0;
    }

    @Override
    public TaskResult getResultIfQoRGoodEnough() {
        return null;
    }

    @Override
    public void notify(Message message) {

        Assert.assertNotNull(message);
        Assert.assertNotNull(message.getType());
        Assert.assertNotNull(message.getContent());

        if(message.getType().equals(SwineConstants.SMARTCOM_HUMAN_MESSAGE))
            taskResult.addHumanResult(message.getContent());
        else if(message.getType().equals(SwineConstants.SMARTCOM_SOFTWARE_MESSAGE))
            taskResult.addSoftwareResult(message.getContent());
    }
}
