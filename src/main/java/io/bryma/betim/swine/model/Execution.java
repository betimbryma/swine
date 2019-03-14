package io.bryma.betim.swine.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import eu.smartsocietyproject.pf.Member;
import io.bryma.betim.swine.config.PigletState;
import io.bryma.betim.swine.piglet.PigletTaskResult;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@Document(collection = "executions")
public class Execution {

    @Id
    private String id;
    private PigletTaskResult pigletTaskResult;
    private String collectiveId;
    @DBRef
    private Set<Member> executors;
    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date endDate;
    private String type;
    private JsonNode json;
    private PigletState state = PigletState.SCHEDULED;
    private String ownerId;
    private String pigletId;

    public Execution() {
    }

    public Execution(String collectiveId, Set<Member> executors,
                     Date startDate, Date endDate, String type, JsonNode json, String ownerId, String pigletId) {
        this.collectiveId = collectiveId;
        this.executors = executors;
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.json = json;
        this.pigletId = pigletId;
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PigletTaskResult getPigletTaskResult() {
        return pigletTaskResult;
    }

    public void setPigletTaskResult(PigletTaskResult pigletTaskResult) {
        this.pigletTaskResult = pigletTaskResult;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
}
