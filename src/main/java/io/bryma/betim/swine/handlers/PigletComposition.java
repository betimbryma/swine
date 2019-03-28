package io.bryma.betim.swine.handlers;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.DTO.NegotiationHandlerDTO;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;
import io.bryma.betim.swine.exceptions.PigletCBTLifecycleException;
import io.bryma.betim.swine.piglet.PigletPlan;

import java.util.Collection;


public class PigletComposition extends AbstractActor implements CompositionHandler {

    private ActorRef parent;
    private ApplicationContext applicationContext;
    private TaskRequest taskRequest;

    private PigletComposition(ApplicationContext context, TaskRequest taskRequest) {
        this.applicationContext = context;
        this.taskRequest = taskRequest;
    }

    static public Props props(ApplicationContext context, TaskRequest taskRequest) {
        return Props.create(PigletComposition.class, () ->
                new PigletComposition(context, taskRequest));
    }

    @Override
    public void preStart() {
        this.parent = getContext().getParent();
    }

    @Override
    public void compose(ApplicationContext context, ApplicationBasedCollective provisioned, TaskRequest t) throws CBTLifecycleException {
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
