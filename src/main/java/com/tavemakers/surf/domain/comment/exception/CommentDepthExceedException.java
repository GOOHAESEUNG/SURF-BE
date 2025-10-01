package com.tavemakers.surf.domain.comment.exception;

import com.tavemakers.surf.global.common.exception.BaseException;

public class CommentDepthExceedException extends BaseException {
    public CommentDepthExceedException() {
        super(ErrorMessage.COMMENT_DEPTH_EXCEEDED.getStatus(), ErrorMessage.COMMENT_DEPTH_EXCEEDED.getMessage());
    }
}
