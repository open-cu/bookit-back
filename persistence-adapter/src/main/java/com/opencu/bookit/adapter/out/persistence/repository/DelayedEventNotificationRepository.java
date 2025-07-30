package com.opencu.bookit.adapter.out.persistence.repository;

import com.opencu.bookit.adapter.out.persistence.entity.EventNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DelayedEventNotificationRepository extends JpaRepository<EventNotificationEntity, UUID> {
    List<EventNotificationEntity> findByUserIdAndEventId(UUID userId, UUID eventId);
}