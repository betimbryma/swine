package io.bryma.betim.swine.piglet;

import akka.actor.AbstractActor;
import eu.smartsocietyproject.pf.TaskResult;

import java.util.*;

public class PigletInstance extends AbstractActor {

   private Date startDate;
   private Date endDate;
   private PigletTaskRequest pigletTaskRequest;


    @Override
    public Receive createReceive() {

        return receiveBuilder()

                .build();
    }
}
