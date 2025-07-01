package ru.tbank.bookit.book_it_backend.mapper;

import org.mapstruct.*;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.DTO.EventResponse;
import ru.tbank.bookit.book_it_backend.model.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "availablePlaces", source = "available_places")
    @Mapping(target = "registeredUsers", expression = "java(mapUsersToUuids(event.getUsers()))")
    EventResponse toEventResponse(Event event);
    List<EventResponse> toEventResponseList(List<Event> events);

    default Set<UUID> mapUsersToUuids(Set<User> users) {
        if (users == null) return Set.of();
        return users.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
    }
}