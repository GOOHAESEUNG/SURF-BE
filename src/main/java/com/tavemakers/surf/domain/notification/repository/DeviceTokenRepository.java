package com.tavemakers.surf.domain.notification.repository;

import com.tavemakers.surf.domain.notification.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    Optional<DeviceToken> findByToken(String token);
}