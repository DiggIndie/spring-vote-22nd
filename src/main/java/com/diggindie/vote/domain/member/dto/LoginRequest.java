package com.diggindie.vote.domain.member.dto;

public record LoginRequest(
        String loginId,
        String password
) {
}
