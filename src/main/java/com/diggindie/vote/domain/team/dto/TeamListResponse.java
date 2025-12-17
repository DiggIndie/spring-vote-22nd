package com.diggindie.vote.domain.team.dto;

import java.util.List;

public record TeamListResponse(
        List<TeamDto> teams
) {
}

