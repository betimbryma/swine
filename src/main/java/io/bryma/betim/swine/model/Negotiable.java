package io.bryma.betim.swine.model;



import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Negotiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String peer;
    private boolean completed;
    private boolean agreed;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "negotiation", nullable = false, updatable = false)
    private Negotiation negotiation;

    public Negotiable() {
    }

    public Negotiable(String peer, Negotiation negotiation) {
        this.peer = peer;
        this.negotiation = negotiation;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isAgreed() {
        return agreed;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }

    public Negotiation getNegotiation() {
        return negotiation;
    }

    public void setNegotiation(Negotiation negotiation) {
        this.negotiation = negotiation;
    }
}
