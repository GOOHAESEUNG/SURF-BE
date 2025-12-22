package com.tavemakers.surf.domain.notification.service;

import com.tavemakers.surf.domain.notification.dto.res.NotificationResDTO;
import com.tavemakers.surf.domain.notification.entity.Notification;
import com.tavemakers.surf.domain.notification.entity.NotificationCategory;
import com.tavemakers.surf.domain.notification.entity.NotificationType;
import com.tavemakers.surf.domain.notification.exception.NotificationNotFoundException;
import com.tavemakers.surf.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationQueryService notificationQueryService;

    @Transactional(readOnly = true)
    public List<NotificationResDTO> getNotifications(Long memberId, NotificationCategory category) {

        List<Notification> notifications;

        if (category == null) {
            // 전체 알림
            notifications = notificationRepository.findByMemberIdOrderByIdDesc(memberId);
        } else {
            // 해당 카테고리의 타입 목록 추출
            List<NotificationType> types = Arrays.stream(NotificationType.values())
                    .filter(t -> t.getCategory() == category)
                    .toList();

            notifications = notificationRepository.findByMemberIdAndTypeInOrderByIdDesc(memberId, types);
        }

        return notifications.stream()
                .map(notificationQueryService::toDto)
                .toList();
    }

    @Transactional
    public void markAsRead(Long notificationId, Long memberId) {
        int updated = notificationRepository.markAsRead(notificationId, memberId);

        if (updated == 0) {
            boolean exists = notificationRepository.existsByIdAndMemberId(notificationId, memberId);
            if (!exists) {
                throw new NotificationNotFoundException();
            }
        }
    }
}
