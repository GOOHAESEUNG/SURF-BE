package com.tavemakers.surf.domain.member.exception;

import com.tavemakers.surf.global.common.exception.BaseException;

import static com.tavemakers.surf.domain.member.exception.ErrorMessage.CAREER_NOT_FOUND;

public class CareerNotFoundException extends BaseException {
    public CareerNotFoundException(String message) {
        super(CAREER_NOT_FOUND.getStatus(), CAREER_NOT_FOUND.getMessage());
    }
}
