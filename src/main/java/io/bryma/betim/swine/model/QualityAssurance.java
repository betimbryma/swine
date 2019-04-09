package io.bryma.betim.swine.model;



import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class QualityAssurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "qualityAssurance")
    private List<QualityAssuranceInstance> qualityAssuranceInstances;
    private boolean done;
    private String actorPath;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "execution_qa", nullable = false, updatable = false)
    private Execution execution_qa;

    public QualityAssurance() {
    }

    public QualityAssurance(Execution execution_qa, String actorPath) {
        this.actorPath = actorPath;
        this.execution_qa = execution_qa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<QualityAssuranceInstance> getQualityAssuranceInstances() {
        return qualityAssuranceInstances;
    }

    public void setQualityAssuranceInstances(List<QualityAssuranceInstance> qualityAssuranceInstances) {
        this.qualityAssuranceInstances = qualityAssuranceInstances;
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

    public Execution getExecution_qa() {
        return execution_qa;
    }

    public void setExecution_qa(Execution execution_qa) {
        this.execution_qa = execution_qa;
    }
}
