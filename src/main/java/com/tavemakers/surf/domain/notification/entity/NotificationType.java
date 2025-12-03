package com.tavemakers.surf.domain.notification.entity;

public enum NotificationType {

    // ===== 활동(ACTIVITY) =====
    POST_LIKE(NotificationCategory.ACTIVITY),           // 본인 작성 글 좋아요
    POST_COMMENT(NotificationCategory.ACTIVITY),        // 본인 작성 글 댓글
    COMMENT_LIKE(NotificationCategory.ACTIVITY),        // 본인 댓글에 좋아요
    COMMENT_REPLY(NotificationCategory.ACTIVITY),       // 본인 댓글에 대댓글
    MESSAGE_RECEIVED(NotificationCategory.ACTIVITY),    // 쪽지
    BADGE_UPDATED(NotificationCategory.ACTIVITY),       // 활동 뱃지 업데이트
    SCORE_UPDATED(NotificationCategory.ACTIVITY),       // 활동 점수 업데이트

    // ===== 일정(SCHEDULE) =====
    EVENT_NOTICE(NotificationCategory.SCHEDULE);        // 행사 공지사항 등록

    private final NotificationCategory category;

    NotificationType(NotificationCategory category) {
        this.category = category;
    }

    public NotificationCategory getCategory() {
        return category;
    }
}