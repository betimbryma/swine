package io.bryma.betim.swine.handlers;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ExecutionHandler;
import eu.smartsocietyproject.smartcom.SmartComServiceRestImpl;
import io.bryma.betim.swine.config.SwineConstants;
import io.bryma.betim.swine.piglet.PigletPlan;
import io.bryma.betim.swine.piglet.PigletTaskResult;
import org.junit.Assert;

import javax.annotation.PostConstruct;

public class PigletExecution extends AbstractActor implements ExecutionHandler {

    private String conversationId;
    private PigletPlan plan;
    private PigletTaskResult taskResult = new PigletTaskResult(); //TODO
    private ActorRef parent;
    private ApplicationContext context;

    public static Props props(ApplicationContext context) {
        return Props.create(PigletExecution.class, () -> new PigletExecution(context));
    }

    private PigletExecution(ApplicationContext context){
        this.context = context;
    }

    @Override
    public void execute(ApplicationContext context, CollectiveWithPlan agreed) throws CBTLifecycleException {

        this.plan = (PigletPlan) agreed.getPlan();
        this.conversationId = plan.getTaskRequest().getId().toString();

        SmartComServiceRestImpl smartComServiceRest = (SmartComServiceRestImpl)
                context.getSmartCom();

        //Identifier identifier = smartComServiceRest.registerNotificationCallback(this);

        //return null;
    }

    @Override
    public Receive createReceive() {
        return null;
    }

    public void notify(Message message) {

        Assert.assertNotNull(message);
        Assert.assertNotNull(message.getType());
        Assert.assertNotNull(message.getContent());
/*
        if(message.getType().equals(SwineConstants.SMARTCOM_HUMAN_MESSAGE))
            taskResult.addHumanResult(message.getContent());
        else if(message.getType().equals(SwineConstants.SMARTCOM_SOFTWARE_MESSAGE))
            taskResult.addSoftwareResult(message.getContent());
            */
    }
}
