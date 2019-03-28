package io.bryma.betim.swine.handlers;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.DTO.NegotiationHandlerDTO;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;
import eu.smartsocietyproject.pf.enummerations.State;
import io.bryma.betim.swine.services.NegotiationService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PigletNegotiation extends AbstractActor implements NegotiationHandler {

    private ActorRef parent;
    private ApplicationContext applicationContext;
    private TaskRequest taskRequest;
    private NegotiationService negotiationService;
    private String url;
    private CollectiveWithPlan collectiveWithPlan;
    private ResidentCollective residentCollective;
    private Set<Member> negotiators;
    private Set<Member> agreed;
    private String kind;
    private Plan plan;

    private PigletNegotiation(ApplicationContext applicationContext, TaskRequest taskRequest, NegotiationService negotiationService, String url) {
        this.applicationContext = applicationContext;
        this.taskRequest = taskRequest;
        this.negotiationService = negotiationService;
        this.url = url;
        this.agreed = new HashSet<>();
        this.negotiators = new HashSet<>();
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
            this.kind = collectiveWithPlan.getCollective().getKind();
            this.plan = collectiveWithPlan.getPlan();
            this.residentCollective
                    = context.getPeerManager()
                    .readCollectiveById(collectiveWithPlan.getCollective().getId());

            this.negotiators = residentCollective.getMembers();

            String negotiationId = negotiationService.createNegotiation(negotiators, taskRequest, getSelf().path());


            String stringBuilder = "Hi, \n You have been invited to participate in a Collective Based Task. " +
                    "Click on the link below for more information:\n" +
                    url + negotiationId +
                    "\n Best regards: \n SmartSociety";

            Message message = new Message.MessageBuilder()
                    .setType("swine")
                    .setSubtype("negotiation")
                    .setContent(stringBuilder)
                    .setSenderId(Identifier.component("swine"))
                    .setReceiverId(Identifier.collective(residentCollective.getId()))
                    .create();

            context.getSmartCom().send(message);
        } catch (CommunicationException | PeerManagerException e) {
            parent.tell(State.NEG_FAIL, getSelf());
            throw new CBTLifecycleException(e.getMessage());
        }

    }

    private void agreed(Member peer) {

        this.agreed.add(peer);

        if (agreed.size() == negotiators.size()) {

            try {
                CollectiveWithPlan agreed = CollectiveWithPlan.of(
                        ApplicationBasedCollective.empty(applicationContext, UUID.randomUUID().toString(), kind)
                                .withMembers(this.agreed), plan
                );
                parent.tell(State.WAITING_FOR_EXECUTION, getSelf());
                parent.tell(agreed, getSelf());
            } catch (Collective.CollectiveCreationException e) {
                parent.tell(State.NEG_FAIL, getSelf());
            }

        }

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NegotiationHandlerDTO.class,
                        negotiationHandlerDTO -> negotiate(applicationContext, negotiationHandlerDTO.getCollectivesWithPlan()))
                .match(Member.class, this::agreed)
                .build();
    }
}
