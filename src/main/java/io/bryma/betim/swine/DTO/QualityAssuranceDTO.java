package io.bryma.betim.swine.DTO;

import io.bryma.betim.swine.model.Peer;

public class QualityAssuranceDTO {

    private String qualityAssuranceId;
    private Peer peer;
    private boolean voted;
    private int score;

    public QualityAssuranceDTO(Peer peer) {
        this.peer = peer;
    }

    public QualityAssuranceDTO() {
    }

    public String getQualityAssuranceId() {
        return qualityAssuranceId;
    }

    public void setQualityAssuranceId(String qualityAssuranceId) {
        this.qualityAssuranceId = qualityAssuranceId;
    }

    public Peer getPeer() {
        return peer;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
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

    public static ImmutableQualityAssuranceDTO of(QualityAssuranceDTO qualityAssuranceDTO){
        return new ImmutableQualityAssuranceDTO(qualityAssuranceDTO.getQualityAssuranceId(),
                qualityAssuranceDTO.getPeer(), qualityAssuranceDTO.isVoted(), qualityAssuranceDTO.getScore());
    }

    public static class ImmutableQualityAssuranceDTO {

        private String qualityAssuranceId;
        private Peer peer;
        private boolean voted;
        private int score;

        private ImmutableQualityAssuranceDTO(String qualityAssuranceId, Peer peer, boolean voted, int score) {
            this.qualityAssuranceId = qualityAssuranceId;
            this.peer = peer;
            this.voted = voted;
            this.score = score;
        }

        public String getQualityAssuranceId() {
            return qualityAssuranceId;
        }

        public Peer getPeer() {
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
