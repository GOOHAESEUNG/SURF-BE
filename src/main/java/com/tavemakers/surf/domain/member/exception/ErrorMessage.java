package com.tavemakers.surf.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 [회원]입니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 [회원]입니다."),
    INVALID_MEMBER_INFO(HttpStatus.BAD_REQUEST, "유효하지 않은 [회원 정보]입니다.");
    TRACK_NOT_FOUND(HttpStatus.NOT_FOUND, "회원의 [트랙]이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String message;

}
