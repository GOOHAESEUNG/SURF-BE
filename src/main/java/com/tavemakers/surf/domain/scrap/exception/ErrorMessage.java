package com.tavemakers.surf.domain.scrap.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 [스크랩]입니다.");

    private final HttpStatus status;
    private final String message;
}
