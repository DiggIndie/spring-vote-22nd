package com.diggindie.vote.domain.member.dto;

public record CandidateApplyResponse(
        Long candidateId,
        String candidateName,
        String candidatePart
) {
}

