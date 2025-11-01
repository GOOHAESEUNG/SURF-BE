package com.tavemakers.surf.global.common.s3.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {

    FILENAME_IS_EMPTY(HttpStatus.NOT_FOUND, "건네받은 [파일명]이 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;

}
