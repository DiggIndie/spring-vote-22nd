package com.diggindie.vote.domain.team.dto;

import com.diggindie.vote.domain.team.dto.TeamVoteResultDto;
import java.util.List;

public record TeamVoteResultResponse(
        List<TeamVoteResultDto> teamVoteResults
) {}
