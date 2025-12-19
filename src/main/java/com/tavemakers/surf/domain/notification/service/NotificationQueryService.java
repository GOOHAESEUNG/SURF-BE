package com.tavemakers.surf.domain.notification.service;

import com.tavemakers.surf.domain.notification.dto.res.NotificationResDTO;
import com.tavemakers.surf.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationQueryService {

    private final NotificationRenderService renderer;

    public NotificationResDTO toDto(Notification n) {
        return new NotificationResDTO(
                n.getId(),
                n.getType(),
                n.getType().getCategory().name(),
                renderer.renderBody(n),
                n.isRead(),
                n.getCreatedAt()
        );
    }
}