package com.tavemakers.surf.domain.comment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 [댓글]입니다."),
    NOT_MY_COMMENT(HttpStatus.FORBIDDEN, "본인의 [댓글]이 아닙니다."),
    INVALID_BLANK_COMMENT(HttpStatus.BAD_REQUEST, "[댓글] 내용은 공백일 수 없습니다."),
    ALREADY_DELETED_COMMENT(HttpStatus.BAD_REQUEST, "이미 삭제된 [댓글]입니다."),
    COMMENT_MENTION_SELF(HttpStatus.BAD_REQUEST, "자기 자신을 멘션할 수 없습니다."),
    INVALID_MENTION_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "회원 멘션은 @으로 시작해야 하며, 이름은 두 글자 이상 입력해주세요. ex) @홍길"),
    CANNOT_REPLY_TO_DELETED_COMMENT(HttpStatus.BAD_REQUEST, "삭제된 [댓글]에는 대댓글을 작성할 수 없습니다."),
    CANNOT_LIKE_DELETED_COMMENT(HttpStatus.BAD_REQUEST, "삭제된 [댓글]에는 좋아요를 누를 수 없습니다.");

    private final HttpStatus status;
    private final String message;

}