package com.tavemakers.surf.global.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/*
* NOTE
* 커스텀 예외를 위한 추상 클래스 명을
* BaseException, BusinessException, etc... 의논하면 좋을 듯.
* */

@Getter
public abstract class BaseException extends RuntimeException {

    private final HttpStatus status;

    public BaseException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

}
