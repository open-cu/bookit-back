package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.domain.model.event.EventApplicationModel;
import com.opencu.bookit.adapter.in.web.dto.response.EventApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventApplicationResponseMapper {
    @Mapping(source = "userModel.id", target = "userId")
    @Mapping(source = "eventModel.id", target = "eventId")
    EventApplicationResponse toResponse(EventApplicationModel model);
}

