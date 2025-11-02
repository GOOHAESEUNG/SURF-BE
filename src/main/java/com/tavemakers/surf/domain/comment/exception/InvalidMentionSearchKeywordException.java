package com.tavemakers.surf.domain.comment.exception;

import com.tavemakers.surf.global.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidMentionSearchKeywordException extends BaseException {

    public InvalidMentionSearchKeywordException() {
        super(HttpStatus.BAD_REQUEST, "회원 멘션은 @으로 시작해야 하며, 이름은 두 글자 이상 입력해주세요. ex) @홍길");
    }
}
