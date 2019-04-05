package io.bryma.betim.swine.handlers.qaHandler;

import akka.actor.AbstractActor;
import akka.actor.Props;
import eu.smartsocietyproject.DTO.ResultDTO;
import eu.smartsocietyproject.pf.ApplicationContext;
import eu.smartsocietyproject.pf.TaskResult;
import eu.smartsocietyproject.pf.enummerations.State;

public class QAQualityAssuranceHandler extends AbstractActor implements eu.smartsocietyproject.pf.cbthandlers.QualityAssuranceHandler {

    public static Props props(){
        return Props.create(QAQualityAssuranceHandler.class, QAQualityAssuranceHandler::new);
    }

    private QAQualityAssuranceHandler(){}

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(TaskResult.class, taskResult -> {
                    try{
                        double qor = Double.parseDouble(taskResult.getResult());
                        getContext().parent().tell(new ResultDTO(qor, taskResult.getResult()), getSelf());
                    } catch(NumberFormatException n){
                        getContext().parent().tell(State.QUALITY_ASSURANCE_FAIL, getSelf());
                    }

        }).build();
    }

    @Override
    public void qualityAssurance(ApplicationContext context, TaskResult taskResult) {

    }
}
