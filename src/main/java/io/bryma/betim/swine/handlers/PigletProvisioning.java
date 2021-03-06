package io.bryma.betim.swine.handlers;

import akka.actor.*;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;
import eu.smartsocietyproject.pf.enummerations.State;
import io.bryma.betim.swine.DTO.Death;

import java.time.Duration;
import java.util.Optional;

public class PigletProvisioning extends AbstractActorWithTimers implements ProvisioningHandler {

    private ActorRef parent;
    private ApplicationContext applicationContext;
    private TaskRequest taskRequest;
    private final String TICK = "TICK";
    private Duration duration;

    private PigletProvisioning(ApplicationContext applicationContext, TaskRequest taskRequest, Duration duration) {
        this.applicationContext = applicationContext;
        this.taskRequest = taskRequest;
        this.duration = duration;
    }

    public static Props props(ApplicationContext applicationContext, TaskRequest taskRequest, Duration duration){
        return Props.create(PigletProvisioning.class, () -> new PigletProvisioning(applicationContext, taskRequest, duration));
    }

    @Override
    public void preStart() {
        this.parent = getContext().parent();
    }

    @Override
    public void provision(ApplicationContext context, TaskRequest t, Optional<Collective> inputCollective) {
        if(duration != null && duration.getSeconds() >= 1)
            getTimers().startSingleTimer(TICK, new Death(), duration);
        try {
            Collective collective = inputCollective.orElseThrow(CBTLifecycleException::new);
            ApplicationBasedCollective applicationBasedCollective = collective.toApplicationBasedCollective();
            context.getPeerManager().persistCollective(applicationBasedCollective);
            parent.tell(applicationBasedCollective, getSelf());
        } catch (CBTLifecycleException | PeerManagerException e) {
            parent.tell(State.PROV_FAIL, getSelf());
        }

    }

    private void provision(Collective inputCollective) {
        provision(applicationContext, taskRequest, Optional.ofNullable(inputCollective));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Collective.class,
                        this::provision)
                .match(Death.class,
                death -> {
                    this.parent.tell(State.PROV_FAIL, getSelf());
                })
                .build();
    }
}
