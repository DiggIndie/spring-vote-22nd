package com.diggindie.vote.domain.member.dto;

public record TokenReissueResponse(
        String accessToken,
        Long expiresIn
) {
}

