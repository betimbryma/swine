package io.bryma.betim.swine.handlers;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;
import eu.smartsocietyproject.pf.enummerations.State;

import java.util.Optional;

public class PigletProvisioning extends AbstractActor implements ProvisioningHandler {

    private ActorRef parent;
    private ApplicationContext applicationContext;
    private TaskRequest taskRequest;

    private PigletProvisioning(ApplicationContext applicationContext, TaskRequest taskRequest) {
        this.applicationContext = applicationContext;
        this.taskRequest = taskRequest;
    }

    public static Props props(ApplicationContext applicationContext, TaskRequest taskRequest){
        return Props.create(PigletProvisioning.class, () -> new PigletProvisioning(applicationContext, taskRequest));
    }

    @Override
    public void preStart() {
        this.parent = getContext().getParent();
    }

    @Override
    public void provision(ApplicationContext context, TaskRequest t, Optional<Collective> inputCollective) throws CBTLifecycleException {

        try {
            Collective collective = inputCollective.orElseThrow(CBTLifecycleException::new);
            ApplicationBasedCollective applicationBasedCollective = collective.toApplicationBasedCollective();
            context.getPeerManager().persistCollective(applicationBasedCollective);
            parent.tell(applicationBasedCollective, getSelf());
        } catch (PeerManagerException e) {
            parent.tell(State.PROV_FAIL, getSelf());
        }

    }

    private void provision(Collective inputCollective) throws CBTLifecycleException {
        provision(applicationContext, taskRequest, Optional.ofNullable(inputCollective));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Collective.class,
                        this::provision)
                .build();
    }
}
