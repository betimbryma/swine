package io.bryma.betim.swine.DTO;


public class ExecutionDTO {

    private Long executionId;
    private String result;
    private String request;
    private boolean done;

    public ExecutionDTO(Long executionId, String result, String request, boolean done) {
        this.executionId = executionId;
        this.result = result;
        this.request = request;
        this.done = done;
    }

    public ExecutionDTO() {
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
