package io.bryma.betim.swine.DTO;

import eu.smartsocietyproject.pf.TaskRequest;
import io.bryma.betim.swine.config.Vote;

public class NegotiationDTO {

    private Long negotiationID;
    private String taskRequest;
    private boolean agreed;
    private boolean done;

    public NegotiationDTO() {
    }

    public NegotiationDTO(Long negotiationID, String taskRequest, boolean agreed, boolean done) {
        this.negotiationID = negotiationID;
        this.taskRequest = taskRequest;
        this.agreed = agreed;
        this.done = done;
    }

    public String getTaskRequest() {
        return taskRequest;
    }

    public void setTaskRequest(String taskRequest) {
        this.taskRequest = taskRequest;
    }

    public boolean isAgreed() {
        return agreed;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }

    public Long getNegotiationID() {
        return negotiationID;
    }

    public void setNegotiationID(Long negotiationID) {
        this.negotiationID = negotiationID;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
