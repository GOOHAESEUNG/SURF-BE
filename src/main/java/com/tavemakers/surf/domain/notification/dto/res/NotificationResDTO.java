package com.tavemakers.surf.domain.notification.dto.res;

import com.tavemakers.surf.domain.notification.entity.Notification;
import com.tavemakers.surf.domain.notification.entity.NotificationType;

public record NotificationResDTO (
        Long id,
        NotificationType type,
        String category,       // ACTIVITY / SCHEDULE
        String body,
        boolean read,
        String createdAt
){
    public static NotificationResDTO from(Notification n) {
        return new NotificationResDTO(
                n.getId(),
                n.getType(),
                n.getType().getCategory().name(),
                n.getBody(),
                n.isRead(),
                n.getCreatedAt().toString()
        );
    }
}
