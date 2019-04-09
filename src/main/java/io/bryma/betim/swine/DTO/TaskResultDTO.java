package io.bryma.betim.swine.DTO;

public class TaskResultDTO {
    private String peer;
    private String result;

    public TaskResultDTO(String peer, String result) {
        this.peer = peer;
        this.result = result;
    }

    public String getPeer() {
        return peer;
    }

    public String getResult() {
        return result;
    }
}
