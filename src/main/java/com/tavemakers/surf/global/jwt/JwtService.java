package com.tavemakers.surf.global.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface JwtService {

    String createAccessToken(Long memberId, String role);
    String createRefreshToken(Long memberId);

    Optional<String> extractAccessTokenFromCookie(HttpServletRequest request);

    Optional<String> extractRefreshToken(HttpServletRequest request);

    Optional<Long> extractMemberId(String token);
    boolean isTokenValid(String token);
    long getExpiration(String token);

    void sendAccessAndRefreshToken(HttpServletResponse res, String accessToken, String refreshToken);
}
