package com.tavemakers.surf.domain.comment.exception;

import com.tavemakers.surf.global.common.exception.BaseException;

import static com.tavemakers.surf.domain.comment.exception.ErrorMessage.COMMENT_MENTION_SELF;

public class CommentMentionSelfException extends BaseException {
    public CommentMentionSelfException() {
        super(COMMENT_MENTION_SELF.getStatus(), COMMENT_MENTION_SELF.getMessage());
    }
}
