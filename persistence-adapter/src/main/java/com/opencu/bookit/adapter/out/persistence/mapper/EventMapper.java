package com.opencu.bookit.adapter.out.persistence.mapper;

import com.opencu.bookit.adapter.out.persistence.entity.EventEntity;
import com.opencu.bookit.domain.model.event.EventModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class, AreaMapper.class, BookingMapper.class,
        }, imports = Optional.class)
public interface EventMapper {

    @Mapping(target = "userModels", source = "users")
    @Mapping(target = "formats", source = "formats")
    @Mapping(target = "times", source = "times")
    @Mapping(target = "participationFormats", source = "participationFormats")
    @Mapping(target = "areaModel", source = "area")
    @Mapping(target = "systemBooking", source = "systemBooking")
    @Mapping(target = "fullDescription", source = "full_description")
    @Mapping(target = "shortDescription",
            expression = "java(Optional.ofNullable(entity.getShort_description()))")
    EventModel toModel(EventEntity entity);

    @Mapping(target = "users", source = "userModels")
    @Mapping(target = "formats", source = "formats")
    @Mapping(target = "times", source = "times")
    @Mapping(target = "participationFormats", source = "participationFormats")
    @Mapping(target = "area", source = "areaModel")
    @Mapping(target = "systemBooking", source = "systemBooking")
    @Mapping(target = "full_description", source = "fullDescription")
    @Mapping(target = "short_description",
            expression = "java(model.getShortDescription().orElse(null))")
    EventEntity toEntity(EventModel model);

    List<EventModel> toModelList(List<EventEntity> entities);
}