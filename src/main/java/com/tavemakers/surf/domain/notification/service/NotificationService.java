package com.tavemakers.surf.domain.notification.service;

import com.tavemakers.surf.domain.notification.dto.res.NotificationResDTO;
import com.tavemakers.surf.domain.notification.entity.Notification;
import com.tavemakers.surf.domain.notification.entity.NotificationCategory;
import com.tavemakers.surf.domain.notification.entity.NotificationType;
import com.tavemakers.surf.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResDTO> getList(Long memberId) {
        return notificationRepository.findByMemberIdOrderByIdDesc(memberId)
                .stream()
                .map(NotificationResDTO::from)
                .toList();
    }

    public List<NotificationResDTO> getActivity(Long memberId) {
        List<NotificationType> types = Arrays.stream(NotificationType.values())
                .filter(t -> t.getCategory() == NotificationCategory.ACTIVITY)
                .toList();

        List<Notification> list = notificationRepository
                .findByMemberIdAndTypeInOrderByIdDesc(memberId, types);
        return list.stream()
                .map(NotificationResDTO::from)
                .toList();
    }

    public List<NotificationResDTO> getSchedule(Long memberId) {
        List<NotificationType> types = Arrays.stream(NotificationType.values())
                .filter(t -> t.getCategory() == NotificationCategory.SCHEDULE)
                .toList();

        List<Notification> list = notificationRepository
                .findByMemberIdAndTypeInOrderByIdDesc(memberId, types);
        return list.stream()
                .map(NotificationResDTO::from)
                .toList();
    }
}
