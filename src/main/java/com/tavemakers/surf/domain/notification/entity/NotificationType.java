package com.tavemakers.surf.domain.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    POST_LIKE(
            "{actorName}님이 회원님의 게시글에 좋아요를 달았습니다",
            "v1/user/posts/{postId}"
    ),

    POST_COMMENT(
            "{actorName}님이 회원님의 게시글에 댓글을 달았습니다",
            "/posts/{postId}"
    ),

    COMMENT_LIKE(
            "{actorName}님이 회원님의 댓글에 좋아요를 달았습니다",
            "/comments/{commentId}"
    ),

    COMMENT_REPLY(
            "{actorName}님이 회원님의 댓글에 답글을 달았습니다",
            "v1/user/posts/{postId}"
    ),

    MESSAGE(
            "{actorName}님이 쪽지를 보냈습니다",
            "/messages/{roomId}"
    ),

    BADGE_UPDATE(
            "활동 뱃지가 업데이트 되었습니다",
            "/profile/badges"
    ),

    SCORE_UPDATE(
            "활동 점수가 업데이트 되었습니다",
            "/profile"
    ),

    NOTICE(
            "새로운 공지사항이 업데이트 되었습니다",
            "/notices/{noticeId}"
    );

    private final String template;
    private final String deeplink;
}