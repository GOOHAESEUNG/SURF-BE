package com.tavemakers.surf.domain.login;

import lombok.Builder;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그인 성공 시 프론트엔드로 내려주는 응답 DTO
 * - accessToken + 최소한의 사용자 정보만 포함
 * - refreshToken, expiresIn 은 제외 (보안 및 불필요 데이터 제거)
 */

@Schema(description = "인가 코드(code)를 받아 JWT AccessToken과 사용자 정보를 반환합니다.")
@Builder
public record LoginResDto(

        @Schema(description = "JWT Access Token", example = "abcdefg...jwt")
        String accessToken,

        @Schema(description = "사용자 닉네임", example = "홍길동")
        String nickname,

        @Schema(description = "사용자 이메일", example = "hong@example.com")
        String email,

        @Schema(description = "프로필 이미지 URL", example = "https://k.kakaocdn.net/.../img_640x640.jpg")
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
