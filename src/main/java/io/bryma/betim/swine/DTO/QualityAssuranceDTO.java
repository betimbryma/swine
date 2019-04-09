package io.bryma.betim.swine.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.bryma.betim.swine.model.Peer;
import io.bryma.betim.swine.model.QualityAssuranceInstance;

import java.util.List;

public class QualityAssuranceDTO {

    private Long qualityAssuranceId;
    private String taskRequest;
    private boolean voted;
    private int score;
    private List<String> results;

    public QualityAssuranceDTO(Long qualityAssuranceId, String taskRequest, List<String> results) {
        this.qualityAssuranceId = qualityAssuranceId;
        this.taskRequest = taskRequest;
        this.results = results;
    }

    public QualityAssuranceDTO() {
    }

    public Long getQualityAssuranceId() {
        return qualityAssuranceId;
    }

    public void setQualityAssuranceId(Long qualityAssuranceId) {
        this.qualityAssuranceId = qualityAssuranceId;
    }

    public String getTaskRequest() {
        return taskRequest;
    }

    public void setTaskRequest(String taskRequest) {
        this.taskRequest = taskRequest;
    }

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public static ImmutableQualityAssuranceDTO of(QualityAssuranceInstance qa){
        return new ImmutableQualityAssuranceDTO(qa.getQualityAssurance().getId(),
                qa.getPeer(), qa.isDone(), qa.getVote());
    }

    public static class ImmutableQualityAssuranceDTO {

        private Long qualityAssuranceId;
        private String peer;
        private boolean voted;
        private int score;

        private ImmutableQualityAssuranceDTO(Long qualityAssuranceId, String peer, boolean voted, int score) {
            this.qualityAssuranceId = qualityAssuranceId;
            this.peer = peer;
            this.voted = voted;
            this.score = score;
        }

        public Long getQualityAssuranceId() {
            return qualityAssuranceId;
        }

        public String getPeer() {
            return peer;
        }

        public boolean isVoted() {
            return voted;
        }

        public int getScore() {
            return score;
        }
    }
}
