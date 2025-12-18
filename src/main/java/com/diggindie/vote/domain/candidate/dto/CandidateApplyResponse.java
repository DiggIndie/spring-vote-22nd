package com.diggindie.vote.domain.candidate.dto;

public record CandidateApplyResponse(
        Long candidateId,
        String candidateName,
        String candidatePart
) {
}

