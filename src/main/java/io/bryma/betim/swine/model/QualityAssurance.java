package io.bryma.betim.swine.model;

import io.bryma.betim.swine.DTO.QualityAssuranceDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "qualityAssurance")
public class QualityAssurance {

    @Id
    private String id;
    private String taskRequest;
    private Set<QualityAssuranceDTO> qualityAssuranceVoters = new HashSet<>();
    private boolean done;
    private String actorPath;

    public QualityAssurance(String taskRequest,
                            Set<QualityAssuranceDTO> qualityAssuranceVoters, String actorPath) {
        this.taskRequest = taskRequest;
        this.qualityAssuranceVoters = qualityAssuranceVoters;
        this.actorPath = actorPath;
    }

    public QualityAssurance() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskRequest() {
        return taskRequest;
    }

    public void setTaskRequest(String taskRequest) {
        this.taskRequest = taskRequest;
    }

    public Set<QualityAssuranceDTO> getQualityAssuranceVoters() {
        return qualityAssuranceVoters;
    }

    public void setQualityAssuranceVoters(Set<QualityAssuranceDTO> qualityAssuranceVoters) {
        this.qualityAssuranceVoters = qualityAssuranceVoters;
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
