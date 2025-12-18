package com.diggindie.vote.domain.member.controller;


import com.diggindie.vote.common.code.SuccessCode;
import com.diggindie.vote.common.config.security.CustomUserDetails;
import com.diggindie.vote.common.response.Response;
import com.diggindie.vote.domain.member.dto.LoginRequest;
import com.diggindie.vote.domain.member.dto.LoginResponse;
import com.diggindie.vote.domain.member.dto.LogoutResponse;
import com.diggindie.vote.domain.member.dto.SignupRequest;
import com.diggindie.vote.domain.member.dto.SignupResponse;
import com.diggindie.vote.domain.member.dto.TokenReissueResponse;
import com.diggindie.vote.domain.member.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다. 가입 성공 시 자동 로그인되어 토큰이 발급됩니다.")
    @PostMapping("/auth/signup")
    public ResponseEntity<Response<SignupResponse>> signup(
            @RequestBody SignupRequest signupRequest,
            HttpServletResponse httpResponse
    ) {

        Response<SignupResponse> response = Response.of(
                SuccessCode.INSERT_SUCCESS,
                true,
                "회원 가입 API",
                authService.signup(signupRequest, httpResponse)
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다. Access Token은 응답 바디에, Refresh Token은 쿠키에 설정됩니다.")
    @PostMapping("/auth/login")
    public ResponseEntity<Response<LoginResponse>> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse httpResponse
    ) {

        Response<LoginResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "일반 로그인 API",
                authService.login(loginRequest, httpResponse)
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "로그아웃", description = "로그아웃합니다. Refresh Token 쿠키가 제거됩니다. 인증된 사용자만 접근 가능합니다.")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/auth/logout")
    public ResponseEntity<Response<LogoutResponse>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletResponse httpResponse
    ) {

        Response<LogoutResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "로그아웃 API",
                authService.logout(httpResponse, userDetails.getExternalId())
        );
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 이용해 새로운 Access Token과 Refresh Token을 발급받습니다.")
    @PostMapping("/auth/reissue")
    public ResponseEntity<Response<TokenReissueResponse>> reissue(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {

        Response<TokenReissueResponse> response = Response.of(
                SuccessCode.GET_SUCCESS,
                true,
                "토큰 재발급 API",
                authService.reissue(httpRequest, httpResponse)
        );
        return ResponseEntity.ok().body(response);
    }

}
