package com.diggindie.vote.domain.candidate.dto;

import jakarta.validation.constraints.NotNull;

public record PartVoteRequestDto(
        @NotNull(message = "후보자 ID는 필수입니다")
        Long candidateId
) {}
