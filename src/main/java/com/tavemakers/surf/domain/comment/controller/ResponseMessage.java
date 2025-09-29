package com.tavemakers.surf.domain.comment.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    COMMENT_CREATED("[댓글]이 성공적으로 생성되었습니다."),
    COMMENT_READ("[댓글]이 성공적으로 조회되었습니다."),
    COMMENT_UPDATED("[댓글]이 성공적으로 수정되었습니다."),
    COMMENT_DELETED("[댓글]이 성공적으로 삭제되었습니다.");

    private final String message;
}
