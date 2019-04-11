package io.bryma.betim.swine.handlers;

import akka.actor.*;
import at.ac.tuwien.dsg.smartcom.exception.CommunicationException;
import at.ac.tuwien.dsg.smartcom.model.Identifier;
import at.ac.tuwien.dsg.smartcom.model.Message;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.DTO.ExecutionHandlerDTO;
import eu.smartsocietyproject.DTO.NegotiationHandlerDTO;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;
import eu.smartsocietyproject.pf.enummerations.State;
import io.bryma.betim.swine.DTO.Death;
import io.bryma.betim.swine.DTO.MemberDTO;
import io.bryma.betim.swine.exceptions.NegotiationException;
import io.bryma.betim.swine.services.NegotiationService;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PigletNegotiation extends AbstractActorWithTimers implements NegotiationHandler {

    private ActorRef parent;
    private final ApplicationContext applicationContext;
    private final NegotiationService negotiationService;
    private Set<Member> negotiators;
    private final Set<Member> agreed;
    private final Set<MemberDTO> responses;
    private String kind;
    private Plan plan;
    private final String TICK = "TICK";
    private final Duration duration;
    private final Long executionID;

    private PigletNegotiation(ApplicationContext applicationContext, Long executionID,
                              NegotiationService negotiationService, Duration duration) {
        this.applicationContext = applicationContext;
        this.executionID = executionID;
        this.agreed = new HashSet<>();
        this.negotiators = new HashSet<>();
        this.responses =  new HashSet<>();
        this.negotiationService = negotiationService;
        this.duration = duration;
    }

    static public Props props(ApplicationContext applicationContext, Long executionID,
                              NegotiationService negotiationService, Duration duration) {
        return Props.create(PigletNegotiation.class,
                () -> new PigletNegotiation(applicationContext, executionID, negotiationService,
                        duration));
    }

    @Override
    public void preStart() {
        this.parent = getContext().getParent();
    }

    @Override
    public void negotiate(ApplicationContext context, ImmutableList<CollectiveWithPlan> negotiables) {
        if(duration != null && duration.getSeconds() >= 1)
            getTimers().startSingleTimer(TICK, new Death(), duration);
        try {
            CollectiveWithPlan collectiveWithPlan = negotiables.get(0);
            this.kind = collectiveWithPlan.getCollective().getKind();
            this.plan = collectiveWithPlan.getPlan();
            ResidentCollective residentCollective = context.getPeerManager()
                    .readCollectiveById(collectiveWithPlan.getCollective().getId());

            this.negotiators = residentCollective.getMembers();
            Long negotiationId;
            try {
                negotiationId = negotiationService.createNegotiation(negotiators, executionID, getSelf().path());
            } catch (NegotiationException e) {
                parent.tell(State.NEG_FAIL, getSelf());
                return;
            }


            String stringBuilder = "Hi, \n\nYou have been invited to participate in a Collective Based Task. " +
                    "Check out the link below for more information:\n" +
                    negotiationService.getUrl() + negotiationId.toString() +
                    "\n \n Best regards: \n SmartSociety";

            Message message = new Message.MessageBuilder()
                    .setType("Swine - Negotiation")
                    .setContent(stringBuilder)
                    .setReceiverId(Identifier.collective(collectiveWithPlan.getCollective().getId()))
                    .create();

            context.getSmartCom().send(message);
        } catch (PeerManagerException | CommunicationException e) {
            parent.tell(State.NEG_FAIL, getSelf());
        }

    }

    private void agreed(MemberDTO dto) {

        responses.add(dto);

        if (responses.size() == negotiators.size()) {

            try {
                CollectiveWithPlan agreed = CollectiveWithPlan.of(
                        ApplicationBasedCollective.empty(applicationContext, UUID.randomUUID().toString(), kind)
                                .withMembers(this.responses
                                .stream().filter(MemberDTO::isAgreed).map(MemberDTO::getMember).collect(Collectors.toSet())), plan
                );
                try {
                    applicationContext.getPeerManager().persistCollective(agreed.getCollective());
                } catch (PeerManagerException e) {
                    parent.tell(State.NEG_FAIL, getSelf());
                    return;
                }
                ExecutionHandlerDTO executionHandlerDTO = new ExecutionHandlerDTO(agreed);
                parent.tell(executionHandlerDTO, getSelf());
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
                .match(MemberDTO.class, this::agreed)
                .match(Death.class,
                        death -> {
                    this.parent.tell(State.NEG_FAIL, getSelf());
                })
                .build();
    }
}
