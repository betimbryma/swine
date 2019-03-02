package io.bryma.betim.swine.services.Scenario1;

import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.Plan;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.Collection;

public class S1Plan extends Plan {

    private Collection<Member> humans;
    private TaskRequest request;

    public S1Plan(Collection<Member> humans, TaskRequest taskRequest) {
        this.humans = humans;
        this.request = taskRequest;
    }

    public Collection<Member> getHumans() {
        return humans;
    }

    public TaskRequest getRequest() {
        return request;
    }
}


