package io.bryma.betim.swine.services.Scenario1;

import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.pf.ApplicationBasedCollective;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.Collective;
import eu.smartsocietyproject.pf.TaskRequest;
import eu.smartsocietyproject.pf.cbthandlers.CBTLifecycleException;
import eu.smartsocietyproject.pf.cbthandlers.ProvisioningHandler;

import java.util.Optional;

public class S1ProvisioningHandler implements ProvisioningHandler {
    @Override
    public ApplicationBasedCollective provision(ApplicationContext applicationContext, TaskRequest taskRequest, Optional<Collective> optional) throws CBTLifecycleException {

        ApplicationBasedCollective abc = optional.get().toApplicationBasedCollective();

        try {
            applicationContext.getPeerManager().persistCollective(abc);

            return abc;
        } catch (PeerManagerException e) {
            throw new CBTLifecycleException(e);
        }
    }
}