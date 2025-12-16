package com.diggindie.vote.domain.member.dto;

import com.diggindie.vote.common.enums.Part;

public record SignupRequest(
        String loginId,
        String password,
        String email,
        Part part,
        String name,
        String team
) {
}
