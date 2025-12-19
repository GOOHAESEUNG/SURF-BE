package com.tavemakers.surf.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tavemakers.surf.domain.notification.entity.Notification;
import com.tavemakers.surf.domain.notification.entity.NotificationType;
import com.tavemakers.surf.domain.notification.repository.NotificationRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationCreateService {
    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Notification create(
            Long receiverId,
            NotificationType type,
            Map<String, Object> payload
    ) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            Notification notification = Notification.of(receiverId, type, payloadJson);
            return notificationRepository.save(notification);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create notification", e);
        }
    }
}
