package com.tavemakers.surf.global.common.exception;

import org.springframework.http.HttpStatus;

/**
 * 인증되지 않은 사용자(또는 승인되지 않은 회원)가 접근했을 때 발생하는 예외
 * - GlobalExceptionHandler에서 401 Unauthorized 응답으로 처리됨
 */
public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
