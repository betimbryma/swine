package io.bryma.betim.swine.services.Scenario1;

import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.CompositionHandler;

import java.util.Collection;
import java.util.List;

public class S1CompositionHandler implements CompositionHandler {

    @Override
    public List<CollectiveWithPlan> compose(ApplicationContext applicationContext, ApplicationBasedCollective applicationBasedCollective, TaskRequest taskRequest) throws CBTLifecycleException {
        try{
            ResidentCollective rc = applicationContext.getPeerManager()
                    .readCollectiveById(applicationBasedCollective.getId());

            Collection<Member> humans = rc.getMembers();

            S1Plan s1Plan = new S1Plan(humans, taskRequest);
            CollectiveWithPlan collectiveWithPlan =
                    CollectiveWithPlan.of(applicationBasedCollective, s1Plan);
            return ImmutableList.of(collectiveWithPlan);
        } catch (PeerManagerException e) {
            throw new CBTLifecycleException(e);
        }
    }
}
