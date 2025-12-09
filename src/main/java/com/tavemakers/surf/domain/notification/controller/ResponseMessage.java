package com.tavemakers.surf.domain.notification.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    NOTIFICATION_READ("[알림]이 성공적으로 조회되었습니다."),
    NOTIFICATION_ACTIVITY_READ("[알림 - 활동]이 성공적으로 조회되었습니다."),
    NOTIFICATION_SCHEDULE_READ("[알림 - 일정]이 성공적으로 조회되었습니다.");

    private final String message;
}
