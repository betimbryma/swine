package io.bryma.betim.swine.piglet;

import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.Plan;
import eu.smartsocietyproject.pf.TaskRequest;

import java.util.Collection;

public class PigletPlan extends Plan {

    Collection<Member> peers;
    TaskRequest taskRequest;

    public Collection<Member> getPeers(){
        return this.peers;
    }

    public TaskRequest getTaskRequest(){
        return this.taskRequest;
    }

    public static class Builder {

        private PigletPlan plan = new PigletPlan();

        public Builder withPeers(Collection<Member> peers){
            plan.peers = peers;

            return this;
        }

        public Builder withTaskRequest(TaskRequest taskRequest){
            plan.taskRequest = taskRequest;
            return this;
        }

        public PigletPlan build(){
            PigletPlan pigletPlan = plan;
            plan = new PigletPlan();
            return pigletPlan;
        }
    }

}
