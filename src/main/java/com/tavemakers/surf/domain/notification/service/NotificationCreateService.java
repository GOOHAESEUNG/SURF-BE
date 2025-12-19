package com.tavemakers.surf.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tavemakers.surf.domain.notification.entity.Notification;
import com.tavemakers.surf.domain.notification.entity.NotificationType;
import com.tavemakers.surf.domain.notification.repository.NotificationRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCreateService {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;
    private final FcmService fcmService;
    private final NotificationRenderService renderer;

    /**
     * 1 알림 저장만 담당
     */
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

    /**
     * 2 알림 저장 + FCM 전송
     */
    @Transactional
    public void createAndSend(
            Long receiverId,
            NotificationType type,
            Map<String, Object> payload
    ) {
        Notification notification = create(receiverId, type, payload);

        // FCM은 실패해도 절대 롤백
        try {
            String body = renderer.renderBody(notification);
            String deeplink = renderer.renderDeeplink(notification);

            fcmService.sendToMember(
                    receiverId,
                    body,
                    deeplink
            );
        } catch (Exception e) {
            // 여기서 swallow
            log.warn("FCM send failed. notificationId={}", notification.getId(), e);
        }
    }
}

