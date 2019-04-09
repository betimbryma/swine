package io.bryma.betim.swine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Negotiation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "negotiation")
    private List<Negotiable> negotiables = new ArrayList<>();
    private boolean done;
    private String actorPath;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "execution_negotiation", nullable = false, updatable = false) //TODO
    private Execution execution_negotiation;

    public Negotiation(Execution execution_negotiation, String actorPath) {
        this.execution_negotiation = execution_negotiation;
        this.actorPath = actorPath;
    }

    public Negotiation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Negotiable> getNegotiables() {
        return negotiables;
    }

    public void setNegotiables(List<Negotiable> negotiables) {
        this.negotiables = negotiables;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getActorPath() {
        return actorPath;
    }

    public void setActorPath(String actorPath) {
        this.actorPath = actorPath;
    }

    public Execution getExecution_negotiation() {
        return execution_negotiation;
    }

    public void setExecution_negotiation(Execution execution_negotiation) {
        this.execution_negotiation = execution_negotiation;
    }
}
