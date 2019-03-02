package io.bryma.betim.swine.piglet;

import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;

import java.util.Optional;

public class PigletProvisioning implements ProvisioningHandler {
    @Override
    public ApplicationBasedCollective provision(ApplicationContext context, TaskRequest t, Optional<Collective> inputCollective) throws CBTLifecycleException {
        return null;
    }
}
