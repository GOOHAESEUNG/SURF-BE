package com.tavemakers.surf.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tavemakers.surf.domain.member.entity.enums.MemberStatus;
import com.tavemakers.surf.domain.member.repository.MemberRepository;
import com.tavemakers.surf.domain.notification.entity.Notification;
import com.tavemakers.surf.domain.notification.entity.NotificationType;
import com.tavemakers.surf.domain.notification.event.NotificationCreatedEvent;
import com.tavemakers.surf.domain.notification.repository.NotificationRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationCreateService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

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
        boolean activeReceiver =
                memberRepository.existsByIdAndStatusNot(receiverId, MemberStatus.WITHDRAWN);
        if (!activeReceiver) return;

        Notification notification = create(receiverId, type, payload);

        eventPublisher.publishEvent(
                new NotificationCreatedEvent(notification.getId(), receiverId)
        );
    }
}

