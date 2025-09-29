package com.tavemakers.surf.domain.comment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 [댓글]입니다."),
    COMMENT_DEPTH_EXCEEDED(HttpStatus.BAD_REQUEST, "[댓글]의 최대 깊이(2)를 초과했습니다."),
    NOT_MY_COMMENT(HttpStatus.FORBIDDEN, "본인의 [댓글]이 아닙니다."),



    private final HttpStatus status;
    private final String message;

}