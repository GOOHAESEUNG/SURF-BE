package com.tavemakers.surf.domain.post.exception;

import com.tavemakers.surf.global.common.exception.BaseException;

import static com.tavemakers.surf.domain.post.exception.ErrorMessage.NOT_MY_POST;

public class NotMyPostException extends BaseException {
    public NotMyPostException() {
        super(NOT_MY_POST.getStatus(), NOT_MY_POST.getMessage());
    }
}
