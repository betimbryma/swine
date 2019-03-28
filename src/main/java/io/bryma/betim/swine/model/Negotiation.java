package io.bryma.betim.swine.model;

import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.pf.Member;
import eu.smartsocietyproject.pf.TaskRequest;
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
    private JsonNode json;
    @DBRef
    private Set<Member> negotiables = new HashSet<>();
    @DBRef
    private Set<Member> agreed = new HashSet<>();
    private boolean done;
    private String actorPath;

    public Negotiation(String type, JsonNode json, Set<Member> negotiables, String actorPath) {
        this.type = type;
        this.json = json;
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

    public JsonNode getJson() {
        return json;
    }

    public void setJson(JsonNode json) {
        this.json = json;
    }

    public Set<Member> getNegotiables() {
        return negotiables;
    }

    public void setNegotiables(Set<Member> negotiables) {
        this.negotiables = negotiables;
    }

    public Set<Member> getAgreed() {
        return agreed;
    }

    public void setAgreed(Set<Member> agreed) {
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
