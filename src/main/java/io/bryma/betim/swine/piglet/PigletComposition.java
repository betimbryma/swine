package io.bryma.betim.swine.piglet;

import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;
import io.bryma.betim.swine.exceptions.PigletCBTLifecycleException;

import java.util.Collection;
import java.util.List;


public class PigletComposition implements CompositionHandler {

    @Override
    public List<CollectiveWithPlan> compose(ApplicationContext context, ApplicationBasedCollective provisioned, TaskRequest t) throws CBTLifecycleException {
        try{
            ResidentCollective residentCollective
                    = context.getPeerManager()
                    .readCollectiveById(provisioned.getId());

            Collection<Member> peers = residentCollective.getMembers();

            PigletPlan plan = new PigletPlan.Builder()
                    .withPeers(peers).withTaskRequest(t).build();

            CollectiveWithPlan collectiveWithPlan
                    = CollectiveWithPlan.of(provisioned, plan);
            return ImmutableList.of(collectiveWithPlan);
        } catch (PeerManagerException e) {
            throw new PigletCBTLifecycleException(e.getMessage());
        }
    }

}
