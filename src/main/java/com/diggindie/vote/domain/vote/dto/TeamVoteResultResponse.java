package com.diggindie.vote.domain.vote.dto;

import java.util.List;

public record TeamVoteResultResponse(
        List<TeamVoteResultDto> teams
) {
}

