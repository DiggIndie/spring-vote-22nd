package com.diggindie.vote.domain.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamVoteRequestDto {

    @NotNull(message = "팀 ID는 필수입니다")
    private Long teamId;
}