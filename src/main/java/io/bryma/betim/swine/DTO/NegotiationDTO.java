package io.bryma.betim.swine.DTO;

import eu.smartsocietyproject.pf.TaskRequest;
import io.bryma.betim.swine.config.Vote;

public class NegotiationDTO {

    private String negotiationID;
    private TaskRequest taskRequest;
    private Vote vote;

    public TaskRequest getTaskRequest() {
        return taskRequest;
    }

    public void setTaskRequest(TaskRequest taskRequest) {
        this.taskRequest = taskRequest;
    }

    public Vote getVote() {
        return vote;
    }

    public void setVote(Vote vote) {
        this.vote = vote;
    }

    public String getNegotiationID() {
        return negotiationID;
    }

    public void setNegotiationID(String negotiationID) {
        this.negotiationID = negotiationID;
    }
}
