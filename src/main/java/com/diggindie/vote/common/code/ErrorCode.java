package com.diggindie.vote.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements Code{

    // 인증 실패
    UNAUTHORIZED_ERROR(401,  "Unauthorized Exception"),

    // 권한 없음
    FORBIDDEN_ERROR(403,  "Forbidden Exception"),

    // JWT / Token errors
    EXPIRED_TOKEN(401, "Expired JWT token"),
    INVALID_TOKEN(401, "Invalid JWT token");

    private final int statusCode;
    private final String message;

}
