package com.diggindie.vote.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CandidateDto(
        Long candidateId,
        String candidateName,
        String candidatePart,
        Long currentVote
) {
}

