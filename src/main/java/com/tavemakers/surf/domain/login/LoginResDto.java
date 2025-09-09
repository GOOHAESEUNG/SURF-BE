package com.tavemakers.surf.domain.login;

import lombok.Builder;
import com.tavemakers.surf.domain.kakao.dto.KakaoTokenResponseDto;

/**
 * 로그인 성공 시 프론트엔드로 내려주는 응답 DTO
 */
@Builder
public record LoginResDto(
        String accessToken,
        String refreshToken,
        String nickname,
        Long expiresIn
) {
    /**
     * 정적 팩토리 메서드
     * - KakaoTokenResponseDto + 사용자 정보 → LoginResDto 변환
     */
    public static LoginResDto from(KakaoTokenResponseDto token, String nickname) {
        return LoginResDto.builder()
                .accessToken(token.accessToken())
                .refreshToken(token.refreshToken())
                .nickname(nickname)
                .expiresIn(token.expiresIn())
                .build();
    }
}
