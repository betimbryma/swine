package io.bryma.betim.swine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Piglet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Peer cannot be null")
    private String owner;
    @NotBlank(message = "Piglet description is required")
    private String description;
    @NotBlank(message = "Piglet name is required")
    private String name;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "piglet")
    private List<Execution> collectiveBasedTasks = new ArrayList<>();

    public Piglet() {
    }

    public Piglet(@NotNull(message = "Peer cannot be null") String owner, @NotBlank(message = "Piglet description is required") String description, @NotBlank(message = "Piglet name is required") String name) {
        this.owner = owner;
        this.description = description;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Execution> getCollectiveBasedTasks() {
        return collectiveBasedTasks;
    }

    public void setCollectiveBasedTasks(List<Execution> collectiveBasedTasks) {
        this.collectiveBasedTasks = collectiveBasedTasks;
    }
}
