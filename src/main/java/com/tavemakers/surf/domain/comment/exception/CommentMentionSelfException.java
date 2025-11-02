package com.tavemakers.surf.domain.comment.exception;

import com.tavemakers.surf.global.common.exception.BaseException;;
import org.springframework.http.HttpStatus;

public class CommentMentionSelfException extends BaseException {
    public CommentMentionSelfException() {
        super(HttpStatus.BAD_REQUEST, "자기 자신을 멘션할 수 없습니다.");
    }
}
