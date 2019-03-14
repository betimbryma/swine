package io.bryma.betim.swine.handlers;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;
import eu.smartsocietyproject.pf.enummerations.State;
import io.bryma.betim.swine.model.Negotiation;
import io.bryma.betim.swine.services.NegotiationService;

public class PigletNegotiation extends AbstractActor implements NegotiationHandler {

    private ActorRef parent;
    private ApplicationContext applicationContext;
    private TaskRequest taskRequest;
    private NegotiationService negotiationService;
    private String url;
    private CollectiveWithPlan collectiveWithPlan;

    private PigletNegotiation(ApplicationContext applicationContext, TaskRequest taskRequest, NegotiationService negotiationService, String url) {
        this.applicationContext = applicationContext;
        this.taskRequest = taskRequest;
        this.negotiationService = negotiationService;
        this.url = url;
    }

    static public Props props(ApplicationContext applicationContext, TaskRequest taskRequest, NegotiationService negotiationService, String url) {
        return Props.create(PigletNegotiation.class,
                () -> new PigletNegotiation(applicationContext, taskRequest, negotiationService, url));
    }

    @Override
    public void preStart() throws Exception {
        this.parent = getContext().getParent();
    }

    @Override
    public void negotiate(ApplicationContext context, ImmutableList<CollectiveWithPlan> negotiables) throws CBTLifecycleException {

        try {
            this.collectiveWithPlan = negotiables.get(0);

            ResidentCollective residentCollective
                    = context.getPeerManager()
                    .readCollectiveById(collectiveWithPlan.getCollective().getId());

            String negotiationId = negotiationService.createNegotiation(residentCollective, taskRequest);


            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Hi, \n You have been invited to participate in a Collective Based Task. ")
                    .append("Click on the link below for more information:\n")
                    .append(url).append(negotiationId)
                    .append("\n Best regards: \n SmartSociety");

            Message message = new Message.MessageBuilder()
                    .setType("swine")
                    .setSubtype("negotiation")
                    .setContent(stringBuilder.toString())
                    .setSenderId(Identifier.component("swine"))
                    .setReceiverId(Identifier.collective(residentCollective.getId()))
                    .create();


                context.getSmartCom().send(message);
        } catch (CommunicationException | PeerManagerException e) {
            parent.tell(State.COMP_FAIL, getSelf());
            throw new CBTLifecycleException(e.getMessage());
        }

    }

    private void notify(Negotiation negotiation) {
        if(negotiation != null)
            parent.tell(collectiveWithPlan, getSelf());
    }

    private void negotiate(ImmutableList<CollectiveWithPlan> negotiables) throws CBTLifecycleException {
        negotiate(applicationContext, negotiables);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Negotiation.class, this::notify)
                .match(ImmutableList.class, this::negotiate)
                .build();
    }
}
