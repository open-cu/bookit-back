package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.ScheduleOverrideEntity;
import com.opencu.bookit.adapter.out.persistence.entity.WeeklyScheduleEntity;
import com.opencu.bookit.domain.model.schedule.ScheduleOverride;
import com.opencu.bookit.domain.model.schedule.WeeklySchedule;
import com.opencu.bookit.domain.model.schedule.WorkingHours;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalTime;
import java.util.Optional;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SchedulePersistenceMapper {

    @Mapping(target = "workingHours", source = "entity")
    WeeklySchedule toDomain(WeeklyScheduleEntity entity);

    @Mapping(target = "workingHours", source = "entity")
    @Mapping(target = "description", expression = "java(java.util.Optional.ofNullable(entity.getDescription()))")
    ScheduleOverride toDomain(ScheduleOverrideEntity entity);

    @Mapping(target = "dayOff", expression = "java(domain.isDayOff())")
    @Mapping(target = "openingTime", source = "workingHours", qualifiedByName = "toOpeningTime")
    @Mapping(target = "closingTime", source = "workingHours", qualifiedByName = "toClosingTime")
    WeeklyScheduleEntity toEntity(WeeklySchedule domain);

    @Mapping(target = "dayOff", expression = "java(domain.isDayOff())")
    @Mapping(target = "openingTime", source = "workingHours", qualifiedByName = "toOpeningTime")
    @Mapping(target = "closingTime", source = "workingHours", qualifiedByName = "toClosingTime")
    @Mapping(target = "description", expression = "java(domain.description().orElse(null))")
    ScheduleOverrideEntity toEntity(ScheduleOverride domain);


    default Optional<WorkingHours> toWorkingHours(WeeklyScheduleEntity entity) {
        if (entity.isDayOff() || entity.getOpeningTime() == null || entity.getClosingTime() == null) {
            return Optional.empty();
        }
        return Optional.of(new WorkingHours(entity.getOpeningTime(), entity.getClosingTime()));
    }

    default Optional<WorkingHours> toWorkingHours(ScheduleOverrideEntity entity) {
        if (entity.isDayOff() || entity.getOpeningTime() == null || entity.getClosingTime() == null) {
            return Optional.empty();
        }
        return Optional.of(new WorkingHours(entity.getOpeningTime(), entity.getClosingTime()));
    }

    @Named("toOpeningTime")
    default LocalTime toOpeningTime(Optional<WorkingHours> workingHours) {
        return workingHours.map(WorkingHours::openingTime).orElse(null);
    }

    @Named("toClosingTime")
    default LocalTime toClosingTime(Optional<WorkingHours> workingHours) {
        return workingHours.map(WorkingHours::closingTime).orElse(null);
    }
}