package com.tavemakers.surf.domain.login.kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Kakao OAuth 토큰 응답 DTO (불변 record 타입)
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoTokenResponseDto(

        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("expires_in")
        Long expiresIn,

        String scope,

        @JsonProperty("refresh_token_expires_in")
        Long refreshTokenExpiresIn,

        @JsonProperty("id_token")
        String idToken // OIDC 켰을 때만 채워짐
) {}
