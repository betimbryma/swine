package io.bryma.betim.swine.handlers;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.ResidentCollective;
import eu.smartsocietyproject.pf.TaskResult;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ExecutionHandler;
import eu.smartsocietyproject.pf.enummerations.State;
import eu.smartsocietyproject.smartcom.SmartComServiceRestImpl;
import io.bryma.betim.swine.config.SwineConstants;
import io.bryma.betim.swine.exceptions.PigletNotFoundException;
import io.bryma.betim.swine.piglet.PigletPlan;
import io.bryma.betim.swine.piglet.PigletTaskResult;
import io.bryma.betim.swine.services.ExecutionService;
import org.junit.Assert;

import javax.annotation.PostConstruct;

public class PigletExecution extends AbstractActor implements ExecutionHandler {

    private PigletTaskResult taskResult = new PigletTaskResult();
    private ActorRef parent;
    private final ApplicationContext context;
    private final ExecutionService executionService;
    private final String executionId;
    private final String url;

    public static Props props(ApplicationContext context, ExecutionService executionService, String executionId, String url) {
        return Props.create(PigletExecution.class, () -> new PigletExecution(context, executionService, executionId, url));
    }

    private PigletExecution(ApplicationContext context, ExecutionService executionService, String executionId, String url){
        this.context = context;
        this.executionService = executionService;
        this.executionId = executionId;
        this.url = url;
    }

    @Override
    public void execute(ApplicationContext context, CollectiveWithPlan agreed) throws CBTLifecycleException {

        String stringBuilder = "Hi, \n You have been invited to participate in a Collective Based Task. " +
                "Click on the link below for more information:\n" +
                url + executionId +
                "\n Best regards: \n SmartSociety";

        try {
            ResidentCollective residentCollective
                    = context.getPeerManager()
                    .readCollectiveById(agreed.getCollective().getId());

            executionService.initiateExecution(residentCollective, executionId, getSelf().path().toString());

            Message message = new Message.MessageBuilder()
                    .setType("swine")
                    .setSubtype("execution")
                    .setContent(stringBuilder)
                    .setSenderId(Identifier.component("swine"))
                    .setReceiverId(Identifier.collective(residentCollective.getId()))
                    .create();

            context.getSmartCom().send(message);

        } catch (PeerManagerException | PigletNotFoundException | CommunicationException e) {
            parent.tell(State.EXEC_FAIL, getSelf());
        }

    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PigletTaskResult.class,
                        pigletTaskResult -> parent.tell(pigletTaskResult, getSelf()))
                .match(CollectiveWithPlan.class,
                        collectiveWithPlan -> execute(context, collectiveWithPlan))
                .build();
    }

}
