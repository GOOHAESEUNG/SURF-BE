package com.tavemakers.surf.domain.notification.repository;

import com.tavemakers.surf.domain.notification.entity.Notification;
import com.tavemakers.surf.domain.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByMemberIdOrderByIdDesc(Long memberId);

    List<Notification> findByMemberIdAndTypeInOrderByIdDesc(Long memberId, Collection<NotificationType> types);
}
