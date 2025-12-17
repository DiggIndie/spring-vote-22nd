package com.diggindie.vote.domain.member.controller;


import com.diggindie.vote.common.code.SuccessCode;
import com.diggindie.vote.common.config.security.CustomUserDetails;
import com.diggindie.vote.common.response.Response;
import com.diggindie.vote.domain.member.dto.LoginRequest;
import com.diggindie.vote.domain.member.dto.LoginResponse;
import com.diggindie.vote.domain.member.dto.LogoutResponse;
import com.diggindie.vote.domain.member.dto.SignupRequest;
import com.diggindie.vote.domain.member.dto.SignupResponse;
import com.diggindie.vote.domain.member.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

}
