package com.tavemakers.surf.domain.post.exception;

import com.tavemakers.surf.global.common.exception.BaseException;

import static com.tavemakers.surf.domain.post.exception.ErrorMessage.POST_ALREADY_DELETED;

public class PostAlreadyDeleteException extends BaseException {
    public PostAlreadyDeleteException() {
        super(POST_ALREADY_DELETED.getStatus(), POST_ALREADY_DELETED.getMessage());
    }
}
