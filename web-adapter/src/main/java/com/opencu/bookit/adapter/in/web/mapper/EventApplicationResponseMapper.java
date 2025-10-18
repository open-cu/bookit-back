package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.domain.model.event.EventApplicationModel;
import com.opencu.bookit.adapter.in.web.dto.response.EventApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventApplicationResponseMapper {
    @Mapping(source = "userModel.id", target = "userId")
    @Mapping(source = "userModel.firstName", target = "userFirstName")
    @Mapping(source = "userModel.lastName", target = "userLastName")
    @Mapping(source = "eventModel.id", target = "eventId")
    @Mapping(source = "eventModel.name", target = "eventName")
    EventApplicationResponse toResponse(EventApplicationModel model);
}

