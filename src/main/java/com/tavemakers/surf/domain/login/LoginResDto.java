package com.tavemakers.surf.domain.login;

import lombok.Builder;

/**
 * 로그인 성공 시 프론트엔드로 내려주는 응답 DTO
 * - accessToken + 최소한의 사용자 정보만 포함
 * - refreshToken, expiresIn 은 제외 (보안 및 불필요 데이터 제거)
 */
@Builder
public record LoginResDto(
        String accessToken,
        String nickname,
        String email,
        String profileImageUrl
) {
    /**
     * 정적 팩토리 메서드
     * - accessToken + nickname → LoginResDto 변환
     */
    public static LoginResDto of(String accessToken, String nickname, String email, String profileImageUrl) {
        return LoginResDto.builder()
                .accessToken(accessToken)
                .nickname(nickname)
                .email(email)
                .profileImageUrl(profileImageUrl)
                .build();

    }
}
