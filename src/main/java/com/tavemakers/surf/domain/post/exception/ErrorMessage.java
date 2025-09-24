package com.tavemakers.surf.domain.post.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 [게시글]입니다.");

    private final HttpStatus status;
    private final String message;

}
