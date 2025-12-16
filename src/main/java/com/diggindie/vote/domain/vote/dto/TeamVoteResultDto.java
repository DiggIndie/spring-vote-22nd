package com.diggindie.vote.domain.vote.dto;

public record TeamVoteResultDto(
        Long teamId,
        String teamName,
        String teamProposal,
        Long currentVote
) {
}

