package io.bryma.betim.swine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "negotiations")
public class Negotiation {

    @Id
    private String id;
    private String type;
    private String request;
    @DBRef
    private Set<Peer> negotiables = new HashSet<>();
    @DBRef
    private Set<Peer> agreed = new HashSet<>();
    private boolean done;
    private String actorPath;

    public Negotiation(String type, String request, Set<Peer> negotiables, String actorPath) {
        this.type = type;
        this.request = request;
        this.negotiables = negotiables;
        this.actorPath = actorPath;
    }

    public Negotiation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Set<Peer> getNegotiables() {
        return negotiables;
    }

    public void setNegotiables(Set<Peer> negotiables) {
        this.negotiables = negotiables;
    }

    public Set<Peer> getAgreed() {
        return agreed;
    }

    public void setAgreed(Set<Peer> agreed) {
        this.agreed = agreed;
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
}
