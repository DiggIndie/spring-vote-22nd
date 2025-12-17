package com.diggindie.vote.domain.member.dto;

import com.diggindie.vote.common.enums.Part;

public record LoginResponse(
        String memberId,
        String name,
        Part part,
        String team,
        String accessToken,
        long expiresIn
) {
}
