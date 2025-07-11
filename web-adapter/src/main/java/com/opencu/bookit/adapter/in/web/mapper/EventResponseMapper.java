package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.EventResponse;
import com.opencu.bookit.domain.model.event.EventModel;
import com.opencu.bookit.domain.model.user.UserModel;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EventResponseMapper {

    @Mapping(target = "availablePlaces", source = "available_places")
    @Mapping(target = "registeredUsers", expression = "java(mapUsersToUuids(event.getUserModels()))")
    EventResponse toEventResponse(EventModel event);

    List<EventResponse> toEventResponseList(List<EventModel> events);

    default Set<UUID> mapUsersToUuids(Set<UserModel> users) {
        if (users == null) return Set.of();
        return users.stream()
                    .map(UserModel::getId)
                    .collect(Collectors.toSet());
    }
}