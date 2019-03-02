package io.bryma.betim.swine.engine;

import akka.actor.AbstractActor;
import eu.smartsocietyproject.runtime.Runtime;
import io.bryma.betim.swine.model.Piglet;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.PostConstruct;

public class PigletInstance extends AbstractActor {

    private TaskExecutor taskExecutor;
    private Runtime runtime;

    public PigletInstance(Runtime runtime) {
        this.runtime = runtime;
    }

    @PostConstruct
    public void init(){
        this.taskExecutor = new TaskExecutorBuilder()
                .corePoolSize(10).build();
    }

    public Piglet call() throws Exception {

        runtime.run();

        return null;
    }

    @Override
    public Receive createReceive() {
        return null;
    }
}
