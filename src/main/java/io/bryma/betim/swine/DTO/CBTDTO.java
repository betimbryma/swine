package io.bryma.betim.swine.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public class CBTDTO {

    @JsonFormat(pattern="dd/MM/yyyy, HH:mm:ss a")
    private long start;
    private boolean openCall;
    private String taskRequest;
    private String name;
    private long provisionTimeout;
    private long compositionTimeout;
    private long negotiationTimeout;
    private long executionTimeout;
    private long qualityAssuranceTimeout;
    private List<JsonNode> queries;
    private List<JsonNode> qaQueries;
    private Double qor;
    private Long pigletId;

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public boolean isOpenCall() {
        return openCall;
    }

    public void setOpenCall(boolean openCall) {
        this.openCall = openCall;
    }

    public String getTaskRequest() {
        return taskRequest;
    }

    public void setTaskRequest(String taskRequest) {
        this.taskRequest = taskRequest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getProvisionTimeout() {
        return provisionTimeout;
    }

    public void setProvisionTimeout(long provisionTimeout) {
        this.provisionTimeout = provisionTimeout;
    }

    public long getCompositionTimeout() {
        return compositionTimeout;
    }

    public void setCompositionTimeout(long compositionTimeout) {
        this.compositionTimeout = compositionTimeout;
    }

    public long getNegotiationTimeout() {
        return negotiationTimeout;
    }

    public void setNegotiationTimeout(long negotiationTimeout) {
        this.negotiationTimeout = negotiationTimeout;
    }

    public long getExecutionTimeout() {
        return executionTimeout;
    }

    public void setExecutionTimeout(long executionTimeout) {
        this.executionTimeout = executionTimeout;
    }

    public long getQualityAssuranceTimeout() {
        return qualityAssuranceTimeout;
    }

    public void setQualityAssuranceTimeout(long qualityAssuranceTimeout) {
        this.qualityAssuranceTimeout = qualityAssuranceTimeout;
    }

    public List<JsonNode> getQueries() {
        return queries;
    }

    public void setQueries(List<JsonNode> queries) {
        this.queries = queries;
    }

    public List<JsonNode> getQaQueries() {
        return qaQueries;
    }

    public void setQaQueries(List<JsonNode> qaQueries) {
        this.qaQueries = qaQueries;
    }

    public Double getQor() {
        return qor;
    }

    public void setQor(Double qor) {
        this.qor = qor;
    }

    public Long getPigletId() {
        return pigletId;
    }

    public void setPigletId(Long pigletId) {
        this.pigletId = pigletId;
    }
}
