package io.bryma.betim.swine.handlers;

import akka.actor.*;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.DTO.NegotiationHandlerDTO;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;
import io.bryma.betim.swine.exceptions.PigletCBTLifecycleException;
import io.bryma.betim.swine.piglet.PigletPlan;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Collection;


public class PigletComposition extends AbstractActorWithTimers implements CompositionHandler {

    private ActorRef parent;
    private ApplicationContext applicationContext;
    private TaskRequest taskRequest;
    private final String TICK = "TICK";
    private Duration duration;

    private PigletComposition(ApplicationContext context, TaskRequest taskRequest, Duration duration) {
        this.applicationContext = context;
        this.taskRequest = taskRequest;
        this.duration = duration;
    }

    static public Props props(ApplicationContext context, TaskRequest taskRequest, Duration duration) {
        return Props.create(PigletComposition.class, () ->
                new PigletComposition(context, taskRequest, duration));
    }

    @Override
    public void preStart() {
        this.parent = getContext().getParent();
    }

    @Override
    public void compose(ApplicationContext context, ApplicationBasedCollective provisioned, TaskRequest t) throws CBTLifecycleException {
        if(duration != null && duration.getSeconds() >= 1)
            getTimers().startSingleTimer(TICK, PoisonPill.getInstance(), duration);
        try{
            ResidentCollective residentCollective
                    = context.getPeerManager()
                    .readCollectiveById(provisioned.getId());

            Collection<Member> peers = residentCollective.getMembers();

            PigletPlan plan = new PigletPlan.Builder()
                    .withPeers(peers).withTaskRequest(t).build();

            CollectiveWithPlan collectiveWithPlan
                    = CollectiveWithPlan.of(provisioned, plan);
            NegotiationHandlerDTO negotiationHandlerDTO = new NegotiationHandlerDTO(ImmutableList.of(collectiveWithPlan));
             parent.tell(negotiationHandlerDTO, getSelf());
        } catch (PeerManagerException e) {
            throw new PigletCBTLifecycleException(e.getMessage());
        }
    }

    private void compose(ApplicationBasedCollective provisioned) throws CBTLifecycleException {
        compose(applicationContext, provisioned, taskRequest);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ApplicationBasedCollective.class,
                        this::compose)
                .build();
    }
}
