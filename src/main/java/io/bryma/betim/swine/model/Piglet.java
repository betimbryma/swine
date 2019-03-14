package io.bryma.betim.swine.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Document(collection = "piglets")
public class Piglet {

    @Id
    private String id;
    @DBRef
    @NotNull(message = "Owner cannot be null")
    @Indexed
    private Peer owner;
    @NotBlank(message = "Piglet description is required")
    private String description;
    @NotBlank(message = "Piglet name is requried")
    private String name;
    @DBRef
    private List<Execution> collectiveBasedTasks;

    public Piglet() {
    }

    public Piglet(@NotNull(message = "Owner cannot be null") Peer owner, @NotBlank(message = "Piglet description is required") String description,
                  @NotBlank(message = "Piglet name is requried") String name, List<Execution> collectiveBasedTasks) {
        this.owner = owner;
        this.description = description;
        this.name = name;
        this.collectiveBasedTasks = collectiveBasedTasks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Peer getOwner() {
        return owner;
    }

    public void setOwner(Peer owner) {
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
