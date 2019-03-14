package io.bryma.betim.swine.handlers;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;
import io.bryma.betim.swine.exceptions.PigletCBTLifecycleException;
import io.bryma.betim.swine.piglet.PigletPlan;
import io.bryma.betim.swine.services.NegotiationService;

import java.util.Collection;


public class PigletComposition extends AbstractActor implements CompositionHandler {

    private ActorRef parent;
    private ApplicationContext applicationContext;
    private TaskRequest taskRequest;
    private NegotiationService negotiationService;
    private String url;

    private PigletComposition(ApplicationContext context, TaskRequest taskRequest, NegotiationService negotiationService
        , String url) {
        this.applicationContext = context;
        this.taskRequest = taskRequest;
        this.negotiationService = negotiationService;
    }

    static public Props props(ApplicationContext context, TaskRequest taskRequest, NegotiationService negotiationService
        , String url) {
        return Props.create(PigletComposition.class, () ->
                new PigletComposition(context, taskRequest, negotiationService, url));
    }

    @Override
    public void preStart() throws Exception {
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
             parent.tell(ImmutableList.of(collectiveWithPlan), getSelf());
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
