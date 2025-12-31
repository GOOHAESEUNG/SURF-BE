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
import java.util.UUID;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_COOKIE_NAME = "refreshToken";
    private static final String ROLE_PREFIX = "ROLE_";

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
                .claim("role", ROLE_PREFIX+role)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpireMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String createRefreshToken(Long memberId, String deviceId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))   // ← memberId만 사용
                .claim("deviceId", deviceId)
                .claim("jti", UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpireMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
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
    public void sendRefreshToken(
            HttpServletResponse res,
            String refreshToken
    ) {
        // TODO 2025.12.28 개발환경이므로 dev, 12.31 prod로 변경
        // TODO 운영 환경에서 prod로 변경
        boolean isProd = "prod".equalsIgnoreCase(activeProfile);
//        boolean isDev = "dev".equalsIgnoreCase(activeProfile);
        // SameSite=None이면 Secure=true가 필수
//        String sameSite = isDev ? "None" : "Lax";
        String sameSite = "Lax";

//        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_COOKIE_NAME, accessToken)
//                .httpOnly(true)
//                .secure(isProd)                       // prod만 Secure
//                .domain(isProd ? ".tavesurf.site" : "localhost")
//                .path("/")
//                .maxAge(Duration.ofMillis(accessTokenExpireMs))
//                .sameSite(sameSite)
//                .build();

//        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
//                .httpOnly(true)
//                .secure(isProd)                       // prod만 Secure
//                .domain(isProd ? ".tavesurf.site" : "localhost")
//                .path("/auth/refresh")
//                .maxAge(Duration.ofMillis(refreshTokenExpireMs))
//                .sameSite(sameSite)
//                .build();

        // res.setHeader(AUTH_HEADER, BEARER_PREFIX + accessToken);
//        res.addHeader("Set-Cookie", accessCookie.toString());
        ResponseCookie.ResponseCookieBuilder builder =
                ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                        .httpOnly(true)
                        .path("/auth/refresh")
                        .maxAge(Duration.ofMillis(refreshTokenExpireMs));

        if (isProd) {
            // 개발배포 / 운영 서버 (tavesurf.site)
            builder
                    .secure(true)
                    .domain(".tavesurf.site")
                    .sameSite("None");   // cross-site 쿠키
        } else {
            // local (localhost)
            builder
                    .secure(false)
                    .sameSite("Lax");    // 기본값
            // ⚠️ domain 설정하지 않음
        }

        ResponseCookie refreshCookie = builder.build();
        res.addHeader("Set-Cookie", refreshCookie.toString());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Optional<String> extractAccessTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank()) {
            return Optional.empty();
        }

        // 표준: Bearer 토큰
        if (header.startsWith("Bearer ")) {
            return Optional.of(header.substring(7));
        }

        // Swagger 테스트 편의: 토큰만 들어온 경우도 허용
        return Optional.of(header);
    }

    public Optional<String> extractDeviceId(String token) {
        try {
            Claims claims = parseClaims(token);
            return Optional.ofNullable(claims.get("deviceId", String.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
