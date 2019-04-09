package io.bryma.betim.swine.DTO;

import eu.smartsocietyproject.pf.Member;

public class MemberDTO {
    private Member member;
    private int vote;
    private boolean agreed;

    public MemberDTO(Member member, boolean agreed) {
        this.member = member;
        this.agreed = agreed;
    }

    public MemberDTO(Member member, int vote) {
        this.member = member;
        this.vote = vote;
    }

    public Member getMember() {
        return member;
    }

    public int getVote() {
        return vote;
    }

    public boolean isAgreed() {
        return agreed;
    }
}
