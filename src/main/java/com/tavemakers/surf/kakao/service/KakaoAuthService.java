package com.tavemakers.surf.kakao.service;

import com.tavemakers.surf.kakao.dto.KakaoTokenResponseDto;

public interface KakaoAuthService {
    String buildAuthorizeUrl(); // 인가 URL 만들기
    KakaoTokenResponseDto exchangeCodeForToken(String code); // 토큰 교환
    String getAccessTokenInfo(String accessToken); // 토큰 유효성 확인
}
