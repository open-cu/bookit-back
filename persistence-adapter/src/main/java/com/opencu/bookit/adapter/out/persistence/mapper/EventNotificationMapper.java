package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.domain.model.event.EventNotification;
import com.opencu.bookit.adapter.out.persistence.entity.EventNotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventNotificationMapper {
    EventNotification toDomain(EventNotificationEntity entity);
    EventNotificationEntity toEntity(EventNotification domain);
}