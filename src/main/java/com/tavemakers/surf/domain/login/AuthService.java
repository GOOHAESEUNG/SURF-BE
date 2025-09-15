package com.tavemakers.surf.domain.login;

/*
 * 소셜 로그인 공통 인터페이스
 * @param <T> 소셜 로그인 토큰 응답 DTO 타입
 */

import reactor.core.publisher.Mono;
import java.util.Map;

public interface AuthService<T, U> {
    String buildAuthorizeUrl();
    Mono<T> exchangeCodeForToken(String code);
    Mono<Map<String, Object>> getAccessTokenInfo(String accessToken); // String → Map
    Mono<U> getUserInfo(String accessToken);
}

