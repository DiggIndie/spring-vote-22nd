package com.diggindie.vote.domain.member.service;

import com.diggindie.vote.common.config.security.jwt.JwtTokenProvider;
import com.diggindie.vote.common.enums.Part;
import com.diggindie.vote.common.enums.Role;
import com.diggindie.vote.domain.member.domain.Member;
import com.diggindie.vote.domain.member.dto.*;
import com.diggindie.vote.domain.member.repository.MemberRepository;
import com.diggindie.vote.domain.team.repository.TeamRepository;
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

    @Value("${jwt.access-token-validity}")
    private Duration accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private Duration refreshTokenValidity;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {

        Member member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(member.getExternalId(), member.getRole());
        setCookies(response, member.getExternalId(), member.getRole());


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
        setCookies(response, savedMember.getExternalId(), savedMember.getRole());

        return new SignupResponse(
                savedMember.getExternalId(),
                savedMember.getMemberName(),
                savedMember.getPart(),
                savedMember.getTeam().getTeamName(),
                accessToken,
                accessTokenValidity.getSeconds()
        );
    }

    public LogoutResponse logout(HttpServletResponse response, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        String externalId = member.getExternalId();
        removeRefreshTokenCookie(response);

        return new LogoutResponse(externalId);
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

    private void setCookies(HttpServletResponse response, String externalId, Role role) {

        String refreshToken = jwtTokenProvider.generateRefreshToken(externalId, role);
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
