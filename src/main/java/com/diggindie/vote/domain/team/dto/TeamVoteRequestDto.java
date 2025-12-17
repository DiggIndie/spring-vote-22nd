package com.diggindie.vote.domain.team.dto;

import jakarta.validation.constraints.NotNull;

public record TeamVoteRequestDto(
        @NotNull(message = "팀 ID는 필수입니다")
        Long teamId
) {}
