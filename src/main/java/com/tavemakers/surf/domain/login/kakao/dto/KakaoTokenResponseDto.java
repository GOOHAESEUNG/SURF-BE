package com.tavemakers.surf.domain.login.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

/**
 * Kakao OAuth 토큰 응답 DTO (불변 record 타입)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoTokenResponseDto(

        String accessToken,
        String tokenType,
        String refreshToken,
        Long expiresIn,
        String scope,
        Long refreshTokenExpiresIn,
        String idToken // OIDC 켰을 때만 채워짐
) {}
