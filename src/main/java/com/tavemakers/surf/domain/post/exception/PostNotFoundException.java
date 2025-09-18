package com.tavemakers.surf.domain.post.exception;

import com.tavemakers.surf.global.common.exception.BaseException;

public class PostNotFoundException extends BaseException {
    public PostNotFoundException() {
        super(ErrorMessage.POST_NOT_FOUND.getStatus(), ErrorMessage.POST_NOT_FOUND.getMessage());
    }
}
