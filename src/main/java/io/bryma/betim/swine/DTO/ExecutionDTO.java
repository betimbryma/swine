package io.bryma.betim.swine.DTO;
import io.bryma.betim.swine.config.PigletState;

import java.util.List;

public class ExecutionDTO {
    private String id;
    private List<String> pigletTaskResult;
    private double qor;
    private PigletState state;

    public ExecutionDTO(String id, List<String> pigletTaskResult, double qor, PigletState state) {
        this.id = id;
        this.pigletTaskResult = pigletTaskResult;
        this.qor = qor;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public List<String> getPigletTaskResult() {
        return pigletTaskResult;
    }

    public double getQor() {
        return qor;
    }

    public PigletState getState() {
        return state;
    }
}
