package com.tavemakers.surf.domain.post.controller;

import com.tavemakers.surf.domain.post.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    POST_CREATED("[게시글]이 성공적으로 생성되었습니다."),
    POST_UPDATED("[게시글]이 성공적으로 수정되었습니다."),
    POST_DELETED("[게시글]이 성공적으로 삭제되었습니다."),
    POST_READ("[게시글]이 성공적으로 조회되었습니다."),
    MY_POSTS_READ("내가 작성한 [게시글] 목록을 성공적으로 조회했습니다."),
    POSTS_BY_BOARD_READ("[게시판]별 [게시글] 목록을 성공적으로 조회했습니다."),
    POSTS_BY_MEMBER_READ("특정 회원이 작성한 [게시글] 목록을 성공적으로 조회했습니다."),

    POST_LIKE_CREATED("[게시글] 좋아요가 성공적으로 추가되었습니다."),
    POST_LIKE_DELETED("[게시글] 좋아요가 성공적으로 취소되었습니다."),

    SCHEDULE_CREATED("[일정]이 성공적으로 생성되었습니다"),
    SCHEDULE_CALENDAR_READ("[일정]이 성공적으로 월별 조회되었습니다.");

    private final String message;

}