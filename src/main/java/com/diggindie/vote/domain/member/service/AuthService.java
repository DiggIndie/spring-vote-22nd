package com.diggindie.vote.domain.member.service;

import com.diggindie.vote.common.config.security.jwt.JwtTokenProvider;
import com.diggindie.vote.common.enums.Part;
import com.diggindie.vote.common.enums.Role;
import com.diggindie.vote.domain.member.domain.Member;
import com.diggindie.vote.domain.member.dto.*;
import com.diggindie.vote.domain.member.repository.MemberRepository;
import com.diggindie.vote.domain.team.repository.TeamRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access-token-validity}")
    private Duration accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private Duration refreshTokenValidity;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {

        Member member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(member.getExternalId(), member.getRole());
        setRefreshToken(response, member.getExternalId(), member.getRole());


        return new LoginResponse(
                member.getExternalId(),
                member.getMemberName(),
                member.getPart(),
                member.getTeam().getTeamName(),
                accessToken,
                accessTokenValidity.getSeconds()
        );
    }

    @Transactional
    public SignupResponse signup(SignupRequest request, HttpServletResponse response) {

        if (memberRepository.existsByLoginId(request.loginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.builder()
                .loginId(request.loginId())
                .password(encodedPassword)
                .email(request.email())
                .part(request.part())
                .memberName(request.name())
                .team(teamRepository.findByTeamName(request.team())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다.")))
                .build();

        Member savedMember = memberRepository.save(member);

        String accessToken = jwtTokenProvider.generateAccessToken(member.getExternalId(), member.getRole());
        setRefreshToken(response, savedMember.getExternalId(), savedMember.getRole());

        return new SignupResponse(
                savedMember.getExternalId(),
                savedMember.getMemberName(),
                savedMember.getPart(),
                savedMember.getTeam().getTeamName(),
                accessToken,
                accessTokenValidity.getSeconds()
        );
    }

    @Transactional(readOnly = true)
    public LogoutResponse logout(HttpServletResponse response, String externalId) {
        refreshTokenService.delete(externalId);
        removeRefreshTokenCookie(response);
        return new LogoutResponse(externalId);
    }

    public TokenReissueResponse reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token이 존재하지 않습니다.");
        }

        String externalId = jwtTokenProvider.parseClaims(refreshToken).getSubject();

        if (!refreshTokenService.validate(externalId, refreshToken)) {
            refreshTokenService.delete(externalId);
            removeRefreshTokenCookie(response);
            throw new IllegalArgumentException("재로그인이 필요합니다.");
        }

        Member member = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        String newAccessToken = jwtTokenProvider.generateAccessToken(externalId, member.getRole());
        setRefreshToken(response, externalId, member.getRole());

        return new TokenReissueResponse(newAccessToken, accessTokenValidity.getSeconds());
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void removeRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void setRefreshToken(HttpServletResponse response, String externalId, Role role) {
        String refreshToken = jwtTokenProvider.generateRefreshToken(externalId, role);
        refreshTokenService.save(externalId, refreshToken);
        addTokenCookie(response, "refreshToken", refreshToken, refreshTokenValidity);
    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }


}
