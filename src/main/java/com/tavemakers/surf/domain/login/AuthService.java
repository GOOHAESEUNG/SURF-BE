package com.tavemakers.surf.domain.login;

/*
 * 소셜 로그인 공통 인터페이스
 * @param <T> 소셜 로그인 토큰 응답 DTO 타입
 * @param <U> 사용자 정보 응답 DTO 타입
 */

import com.tavemakers.surf.global.logging.LogEvent;
import com.tavemakers.surf.global.logging.LogParam;
import com.tavemakers.surf.global.logging.LogPropsProvider;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface AuthService<T, U> {

    /** 인가 URL 생성 (요청 환경 기반) */
    String buildAuthorizeUrl(String redirectUri);
    T exchangeCodeForToken(String code);      // 인가 코드로 토큰 교환
    Map<String, Object> getAccessTokenInfo(String accessToken); // AccessToken 검증
    U getUserInfo(String accessToken);        // 사용자 정보 요청
    void logAuthorize(String loginMethod, String redirectUri);
    void logCallback(String provider, int codeLength);
    void logLoginSuccess(Long userId, String issuedToken);
    void logLoginFailed(int errorCode, String errorMsg);
}
