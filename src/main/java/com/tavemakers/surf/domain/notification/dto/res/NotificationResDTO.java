package com.tavemakers.surf.domain.notification.dto.res;

import com.tavemakers.surf.domain.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResDTO {

    private Long id;
    private NotificationType type;
    private String category;       // ACTIVITY / SCHEDULE
    private String body;
    private boolean read;
    private String createdAt;      // yyyy-MM-dd HH:mm 형태 또는 ISO

}
