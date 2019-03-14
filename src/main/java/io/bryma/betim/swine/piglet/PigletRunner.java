package io.bryma.betim.swine.piglet;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import eu.smartsocietyproject.peermanager.PeerManagerException;
import eu.smartsocietyproject.peermanager.query.PeerQuery;
import eu.smartsocietyproject.peermanager.query.QueryOperation;
import eu.smartsocietyproject.peermanager.query.QueryRule;
import eu.smartsocietyproject.pf.*;
import eu.smartsocietyproject.runtime.Runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Betim Bryma
 */
public class PigletRunner extends AbstractActor {

    private static final Logger logger = LoggerFactory.getLogger(PigletRunner.class);
    private final PigletTaskRequest taskRequest;
    private final SmartSocietyApplicationContext smartSocietyApplicationContext;
    private final PigletApplication pigletApplication;
    private final String collectiveQuery;
    private final TaskFlowDefinition taskFlowDefinition;
    private TaskResult taskResult;
    private ActorRef parent;
    private ActorRef runtime;

    @Override
    public void preStart() throws Exception {
        this.parent = getContext().getParent();
        runtime = getContext().getSystem().actorOf(Runtime.props(smartSocietyApplicationContext, pigletApplication));
    }

    public PigletRunner(PigletTaskRequest taskRequest, SmartSocietyApplicationContext smartSocietyApplicationContext,
                        String collectiveQuery, TaskFlowDefinition taskFlowDefinition, PigletApplication pigletApplication) {
        this.taskRequest = taskRequest;
        this.smartSocietyApplicationContext = smartSocietyApplicationContext;
        this.collectiveQuery = collectiveQuery;
        this.taskFlowDefinition = taskFlowDefinition;
        this.pigletApplication = pigletApplication;
    }



    private void run() {

        try {
            Collective collective = ApplicationBasedCollective
                    .createFromQuery(smartSocietyApplicationContext,
                            PeerQuery.create()
                                    .withRule(QueryRule.create(collectiveQuery)
                                            .withValue(AttributeType.from("true"))
                                            .withOperation(QueryOperation.equals))
                    );

            CollectiveBasedTask collectiveBasedTask
                    = CBTBuilder.from(taskFlowDefinition.withCollectiveForProvisioning(collective))
                    .withTaskRequest(taskRequest).build();

            collectiveBasedTask.start();

        } catch (PeerManagerException e) {
            getContext().getParent().tell(e, getContext().parent());
        }

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        TaskResult.class,
                        t -> {
                            setTaskResult(t);
                            getContext().getParent().tell(t, (getContext().parent()));
                            })
                .matchAny(o -> logger.info("received unknown message"))
                .build();
    }

    public TaskResult getTaskResult() {
        return taskResult;
    }

    private void setTaskResult(TaskResult taskResult) {
        this.taskResult = taskResult;
    }
}
