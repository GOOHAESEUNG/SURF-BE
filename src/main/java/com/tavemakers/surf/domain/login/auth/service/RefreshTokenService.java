package com.tavemakers.surf.domain.login.auth.service;

import jakarta.servlet.http.HttpServletResponse;

public interface RefreshTokenService {

    /** 로그인 시 refresh 발급 + 저장 + 쿠키 세팅 */
    void issue(HttpServletResponse response, Long memberId, String deviceId);

    /**
     * RTR 핵심
     * refresh 검증 + 재사용 탐지 + 회전(rotation)
     * - 기존 refresh 폐기
     * - 새 refresh 발급 및 저장
     * - 쿠키에 새 refresh 세팅
     * @return memberId
     */
    Long rotate(HttpServletResponse response, String refreshToken);

    /** 특정 디바이스 refresh 무효화 (로그아웃) */
    void invalidate(Long memberId, String deviceId);

    /** refresh 재사용 탐지 시 전체 세션 폐기 */
    void invalidateAll(Long memberId);
}
