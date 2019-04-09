package io.bryma.betim.swine.piglet;

import com.google.common.collect.ImmutableList;
import eu.smartsocietyproject.pf.TaskResult;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class PigletTaskResult extends TaskResult {

    private ImmutableList<String> results = ImmutableList.of();

    @Override
    public String getResult() {
        return toString();
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        results.stream().forEach(result -> {stringBuilder.append(result).append("\n");});

        return stringBuilder.toString();
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(ImmutableList<String> results) {
        this.results = results;
    }
}
