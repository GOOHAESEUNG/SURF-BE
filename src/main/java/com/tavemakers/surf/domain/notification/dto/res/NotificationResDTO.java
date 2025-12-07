package com.tavemakers.surf.domain.notification.dto.res;

import com.tavemakers.surf.domain.notification.entity.NotificationType;

public record NotificationResDTO (
        Long id,
    NotificationType type,
    String category,       // ACTIVITY / SCHEDULE
    String body,
    boolean read,
    String createdAt
){
}
