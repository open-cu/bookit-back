package com.opencu.bookit.adapter.out.persistence.adapter;

import com.opencu.bookit.adapter.out.persistence.entity.EventNotificationEntity;
import com.opencu.bookit.adapter.out.persistence.mapper.EventNotificationMapper;
import com.opencu.bookit.adapter.out.persistence.repository.DelayedEventNotificationRepository;
import com.opencu.bookit.application.port.out.nofication.DeleteDelayedNotificationPort;
import com.opencu.bookit.application.port.out.nofication.LoadDelayedEventNotificationPort;
import com.opencu.bookit.application.port.out.nofication.SaveDelayedEventNotificationPort;
import com.opencu.bookit.domain.model.event.EventNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class DelayedDelayedEventNotificationPersistenceAdapter implements SaveDelayedEventNotificationPort, LoadDelayedEventNotificationPort, DeleteDelayedNotificationPort {
    DelayedEventNotificationRepository delayedEventNotificationRepository;
    EventNotificationMapper eventNotificationMapper;

    @Autowired
    public DelayedDelayedEventNotificationPersistenceAdapter(DelayedEventNotificationRepository delayedEventNotificationRepository,
                                                             EventNotificationMapper eventNotificationMapper) {
        this.delayedEventNotificationRepository = delayedEventNotificationRepository;
        this.eventNotificationMapper = eventNotificationMapper;
    }

    @Override
    public Optional<EventNotification> loadById(UUID id) {
        return delayedEventNotificationRepository.findById(id).map(eventNotificationMapper::toDomain);
    }

    @Override
    public EventNotification save(EventNotification eventNotification) {
        return eventNotificationMapper.toDomain(
                delayedEventNotificationRepository.save(eventNotificationMapper.toEntity(eventNotification))
        );
    }

    @Override
    public void cancelNotification(UUID userId, UUID eventId) {
        List<EventNotificationEntity> eventNotificationlist = delayedEventNotificationRepository.findByUserIdAndEventId(userId, eventId);
        if (!eventNotificationlist.isEmpty()) {
            delayedEventNotificationRepository.deleteAll(eventNotificationlist);
        } else {
            log.warn("No notifications found for userId: {} and eventId: {}", userId, eventId);
        }
    }
}
