package com.tavemakers.surf.domain.comment.exception;

import com.tavemakers.surf.global.common.exception.BaseException;

public class CannotReplyToDeletedCommentException extends BaseException {
    public CannotReplyToDeletedCommentException() {
        super(
                ErrorMessage.CANNOT_REPLY_TO_DELETED_COMMENT.getStatus(),
                ErrorMessage.CANNOT_REPLY_TO_DELETED_COMMENT.getMessage()
        );
    }
}

