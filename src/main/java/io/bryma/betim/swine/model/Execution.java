package io.bryma.betim.swine.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.bryma.betim.swine.config.PigletState;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Execution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String collectiveId;
    @JsonFormat(pattern = "yyyy-mm-dd")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-mm-dd")
    private LocalDateTime endDate;
    private String type;
    @NotNull(message = "The request cannot be null")
    @NotEmpty(message = "The request cannot be empty")
    private String request;
    private PigletState state = PigletState.SCHEDULED;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "piglet", nullable = false, updatable = false)
    private Piglet piglet;
    private String actorPath;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "execution")
    private List<TaskResult> taskResults = new ArrayList<>();
    @NotNull(message = "CBT needs to have an owner")
    private String peer;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "execution_negotiation")
    private List<Negotiation> negotiations = new ArrayList<>();
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "execution_qa")
    private List<QualityAssurance> qualityAssurances = new ArrayList<>();

    public Execution() {
    }

    public Execution(LocalDateTime startDate, LocalDateTime endDate, String type,
                     @NotNull(message = "The request cannot be null")
                     @NotEmpty(message = "The request cannot be empty") String request, Piglet piglet, String peer) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.type = type;
        this.request = request;
        this.piglet = piglet;
        this.peer = peer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCollectiveId() {
        return collectiveId;
    }

    public void setCollectiveId(String collectiveId) {
        this.collectiveId = collectiveId;
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

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public PigletState getState() {
        return state;
    }

    public void setState(PigletState state) {
        this.state = state;
    }

    public Piglet getPiglet() {
        return piglet;
    }

    public void setPiglet(Piglet piglet) {
        this.piglet = piglet;
    }

    public String getActorPath() {
        return actorPath;
    }

    public void setActorPath(String actorPath) {
        this.actorPath = actorPath;
    }

    public List<TaskResult> getTaskResults() {
        return taskResults;
    }

    public void setTaskResults(List<TaskResult> taskResults) {
        this.taskResults = taskResults;
    }

    public String getPeer() {
        return peer;
    }

    public void setPeer(String peer) {
        this.peer = peer;
    }

    public List<Negotiation> getNegotiations() {
        return negotiations;
    }

    public void setNegotiations(List<Negotiation> negotiations) {
        this.negotiations = negotiations;
    }

    public List<QualityAssurance> getQualityAssurances() {
        return qualityAssurances;
    }

    public void setQualityAssurances(List<QualityAssurance> qualityAssurances) {
        this.qualityAssurances = qualityAssurances;
    }
}
