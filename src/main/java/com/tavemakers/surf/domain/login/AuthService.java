package com.tavemakers.surf.domain.login;

/**
 * 소셜 로그인 공통 인터페이스
 * @param <T> 소셜 로그인 토큰 응답 DTO 타입
 */

public interface AuthService<T> {
    String buildAuthorizeUrl();   // 인가 URL 만들기
    T exchangeCodeForToken(String code); // 토큰 교환
    String getAccessTokenInfo(String accessToken); // 토큰 유효성 확인
    Object getUserInfo(String accessToken); // 액세스 토큰으로 사용자 정보 조회

}
