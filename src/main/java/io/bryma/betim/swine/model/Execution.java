package io.bryma.betim.swine.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import eu.smartsocietyproject.pf.Member;
import io.bryma.betim.swine.config.PigletState;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "executions")
public class Execution {

    @Id
    private String id;
    private List<String> results = new ArrayList<>();
    private String collectiveId;
    @DBRef
    private Set<Member> executors = new HashSet<>();
    @JsonFormat(pattern = "yyyy-mm-dd")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-mm-dd")
    private LocalDateTime endDate;
    private String type;
    private String request;
    private PigletState state = PigletState.SCHEDULED;
    private String ownerId;
    private String pigletId;
    private String actorPath;

    public Execution() {
    }

    public Execution(LocalDateTime startDate, LocalDateTime endDate,
                     String type, String request, String ownerId, String pigletId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.request = request;
        this.pigletId = pigletId;
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollectiveId() {
        return collectiveId;
    }

    public void setCollectiveId(String collectiveId) {
        this.collectiveId = collectiveId;
    }

    public Set<Member> getExecutors() {
        return executors;
    }

    public void setExecutors(Set<Member> executors) {
        this.executors = executors;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PigletState getState() {
        return state;
    }

    public void setState(PigletState state) {
        this.state = state;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPigletId() {
        return pigletId;
    }

    public void setPigletId(String pigletId) {
        this.pigletId = pigletId;
    }

    public String getActorPath() {
        return actorPath;
    }

    public void setActorPath(String actorPath) {
        this.actorPath = actorPath;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }
}
