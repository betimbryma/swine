package io.bryma.betim.swine.piglet;

import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.CollectiveWithPlan;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.NegotiationHandler;

import java.util.List;

public class PigletNegotiation implements NegotiationHandler {
    @Override
    public CollectiveWithPlan negotiate(ApplicationContext context, List<CollectiveWithPlan> negotiables) throws CBTLifecycleException {
        return null;
    }
}
