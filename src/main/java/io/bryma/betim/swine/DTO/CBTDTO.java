package io.bryma.betim.swine.DTO;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.List;

public class CBTDTO {

    private LocalDateTime startDate;
    private boolean openCall;
    private String taskRequest;
    private String name;
    private Integer provisionTimeout;
    private Integer compositionTimeout;
    private Integer negotiationTimeout;
    private Integer executionTimeout;
    private Integer qualityAssuranceTimeout;
    private boolean qaCollective;
    private String unit;
    private List<JsonNode> queries;
    private String privateKey;
    private Double qor;
    private Integer quantity;
    private String pigletId;
    private String smartContractAddress;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
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

    public Integer getProvisionTimeout() {
        return provisionTimeout;
    }

    public void setProvisionTimeout(Integer provisionTimeout) {
        this.provisionTimeout = provisionTimeout;
    }

    public Integer getCompositionTimeout() {
        return compositionTimeout;
    }

    public void setCompositionTimeout(Integer compositionTimeout) {
        this.compositionTimeout = compositionTimeout;
    }

    public Integer getNegotiationTimeout() {
        return negotiationTimeout;
    }

    public void setNegotiationTimeout(Integer negotiationTimeout) {
        this.negotiationTimeout = negotiationTimeout;
    }

    public Integer getExecutionTimeout() {
        return executionTimeout;
    }

    public void setExecutionTimeout(Integer executionTimeout) {
        this.executionTimeout = executionTimeout;
    }

    public Integer getQualityAssuranceTimeout() {
        return qualityAssuranceTimeout;
    }

    public void setQualityAssuranceTimeout(Integer qualityAssuranceTimeout) {
        this.qualityAssuranceTimeout = qualityAssuranceTimeout;
    }

    public boolean isQaCollective() {
        return qaCollective;
    }

    public void setQaCollective(boolean qaCollective) {
        this.qaCollective = qaCollective;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<JsonNode> getQueries() {
        return queries;
    }

    public void setQueries(List<JsonNode> queries) {
        this.queries = queries;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Double getQor() {
        return qor;
    }

    public void setQor(Double qor) {
        this.qor = qor;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPigletId() {
        return pigletId;
    }

    public void setPigletId(String pigletId) {
        this.pigletId = pigletId;
    }

    public String getSmartContractAddress() {
        return smartContractAddress;
    }

    public void setSmartContractAddress(String smartContractAddress) {
        this.smartContractAddress = smartContractAddress;
    }
}
