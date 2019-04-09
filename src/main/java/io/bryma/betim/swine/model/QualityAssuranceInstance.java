package io.bryma.betim.swine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class QualityAssuranceInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Peer cannot be null")
    private String peer;
    private boolean done;
    private int vote;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "qualityAssurance", nullable = false, updatable = false)
    private QualityAssurance qualityAssurance;

    public QualityAssuranceInstance() {
    }

    public QualityAssuranceInstance(@NotNull(message = "Peer cannot be null") String peer, QualityAssurance qualityAssurance) {
        this.peer = peer;
        this.qualityAssurance = qualityAssurance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPeer() {
        return peer;
    }

    public void setPeer(String peer) {
        this.peer = peer;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public QualityAssurance getQualityAssurance() {
        return qualityAssurance;
    }

    public void setQualityAssurance(QualityAssurance qualityAssurance) {
        this.qualityAssurance = qualityAssurance;
    }
}
