package com.diggindie.vote.domain.team.dto;

public record TeamVoteResultDto(
        Long teamId,
        String teamName,
        String teamProposal,
        Long currentVote
) {
}