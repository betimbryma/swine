package io.bryma.betim.swine.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
public class TaskResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String result;
    @NotBlank(message = "The peer must be identified")
    private String peer;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "execution", nullable = false, updatable = false)
    private Execution execution;
    private boolean done;

    public TaskResult() {
    }

    public TaskResult(@NotBlank(message = "The peer must be identified") String peer, Execution execution) {
        this.peer = peer;
        this.execution = execution;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPeer() {
        return peer;
    }

    public void setPeer(String peer) {
        this.peer = peer;
    }

    public Execution getExecution() {
        return execution;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
