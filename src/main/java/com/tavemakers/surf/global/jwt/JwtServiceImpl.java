package com.tavemakers.surf.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_COOKIE_NAME = "refreshToken";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpireMs;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpireMs;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile; // prod일 때만 Secure+SameSite=None

    private Key secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createAccessToken(Long memberId, String role) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpireMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))   // ← memberId만 사용
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpireMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return Optional.of(header.substring(BEARER_PREFIX.length()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();
        for (Cookie c : cookies) {
            if (REFRESH_COOKIE_NAME.equals(c.getName())) {
                return Optional.ofNullable(c.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> extractMemberId(String token) {
        try {
            Claims claims = parseClaims(token);
            String sub = claims.getSubject(); // subject = memberId
            return Optional.of(Long.parseLong(sub));
        } catch (JwtException | NumberFormatException e) {
            log.error("토큰에서 memberId 추출 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.JwtException e) {
            log.error("토큰 유효성 검사 실패: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public long getExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().getTime();
        } catch (io.jsonwebtoken.JwtException e) {
            log.error("토큰 만료 시간 추출 실패: {}", e.getMessage());
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }

    @Override
    public void sendAccessAndRefreshToken(HttpServletResponse res, String accessToken, String refreshToken) {
        boolean isProd = "prod".equalsIgnoreCase(activeProfile);
        // SameSite=None이면 Secure=true가 필수
        String sameSite = isProd ? "None" : "Lax";

        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(isProd)
                .path("/")
                .maxAge(Duration.ofMillis(refreshTokenExpireMs))
                .sameSite(sameSite)
                .build();

        res.setHeader(AUTH_HEADER, BEARER_PREFIX + accessToken);
        res.addHeader("Set-Cookie", cookie.toString());
    }

    @Override
    public void sendAccessToken(HttpServletResponse res, String newAccessToken) {
        res.setHeader(AUTH_HEADER, BEARER_PREFIX + newAccessToken);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}