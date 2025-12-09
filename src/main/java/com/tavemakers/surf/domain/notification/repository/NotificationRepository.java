package com.tavemakers.surf.domain.notification.repository;

import com.tavemakers.surf.domain.notification.entity.Notification;
import com.tavemakers.surf.domain.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByMemberIdOrderByIdDesc(Long memberId);

    List<Notification> findByMemberIdAndTypeInOrderByIdDesc(Long memberId, Collection<NotificationType> types);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update Notification n
           set n.read = true
         where n.id = :id
           and n.memberId = :memberId
           and n.read = false
        """)
    int markAsRead(@Param("id") Long id, @Param("memberId") Long memberId);

    boolean existsByIdAndMemberId(Long id, Long memberId);
}
