package io.bryma.betim.swine.services.Scenario1;

import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;

import java.util.List;

public class S1NegotiationHandler implements NegotiationHandler {
    @Override
    public CollectiveWithPlan negotiate(ApplicationContext applicationContext, List<CollectiveWithPlan> list) throws CBTLifecycleException {
        return list.get(0);
    }
}