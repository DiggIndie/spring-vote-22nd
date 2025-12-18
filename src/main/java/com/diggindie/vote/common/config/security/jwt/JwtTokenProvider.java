package com.diggindie.vote.common.config.security.jwt;


import com.diggindie.vote.common.config.security.CustomUserDetailService;
import com.diggindie.vote.common.config.security.CustomUserDetails;
import com.diggindie.vote.common.code.ErrorCode;
import com.diggindie.vote.common.exception.CustomException;
import com.diggindie.vote.common.enums.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements InitializingBean {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private Duration accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private Duration refreshTokenValidity;

    private Key key;

    private final CustomUserDetailService customUserDetailService;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String externalId, Role role) {
        return generateToken(externalId, role, accessTokenValidity);
    }

    public String generateRefreshToken(String externalId, Role role) {
        return generateToken(externalId, role, refreshTokenValidity);
    }

    public String generateToken(String externalId, Role role, Duration expiration) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration.toMillis());

        return Jwts.builder()
                .setSubject(externalId)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getAccessToken(HttpServletRequest request) {

        // cookie 기반 토큰 추출
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // header 기반 토큰 추출
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Claims parseClaims(String token) {

        try {
            return Jwts.parser()
                    .verifyWith((SecretKey)key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰
            log.warn("만료된 JWT 토큰이 사용되었습니다.", e);
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (SecurityException | MalformedJwtException e) {
            // 서명 불일치 또는 형식 문제
            log.warn("JWT 토큰 형식이 잘못되었습니다.", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 토큰이 사용되었습니다.", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰의 값이 비어있습니다.", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String getExternalId(String token) {
        return parseClaims(token).getSubject();
    }

    public Role getRole(String token) {
        String roleName = parseClaims(token).get("role", String.class);
        return Role.valueOf(roleName);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token); // 파싱과 동시에 검증 수행
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            // 잘못된 서명 또는 JWT 형식
            log.warn("JWT 토큰 형식이 잘못되었습니다.", e);
        } catch (ExpiredJwtException e) {
            // 만료된 JWT
            log.warn("만료된 JWT 토큰이 사용되었습니다.", e);
        } catch (UnsupportedJwtException e) {
            // 지원하지 않는 JWT
            log.warn("지원하지 않는 JWT 토큰이 사용되었습니다.", e);
        } catch (IllegalArgumentException e) {
            // 빈 JWT 또는 기타 문제
            log.warn("JWT 토큰의 값이 비어있습니다.", e);
        }
        return false;
    }

    public Authentication getAuthentication(String token) {

        String externalId = getExternalId(token);
        Role role = getRole(token);

        CustomUserDetails userDetails = customUserDetailService.loadByExternalId(externalId);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

}
