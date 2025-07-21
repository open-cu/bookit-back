package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.EventResponse;
import com.opencu.bookit.application.service.photo.PhotoService;
import com.opencu.bookit.domain.model.event.EventModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventResponseMapper {
    private final PhotoService service;

    public EventResponseMapper(PhotoService service) {
        this.service = service;
    }

    public EventResponse toEventResponse(EventModel event) throws IOException {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTags(),
                event.getFormats(),
                event.getTimes(),
                event.getParticipationFormats(),
                service.getImagesFromKeys(event.getKeys()),
                event.getDate(),
                event.getAvailable_places()
        );
    }

    public List<EventResponse> toEventResponseList(List<EventModel> events) throws IOException {
        List<EventResponse> eventResponseList = new ArrayList<>();
        for (var event:  events) {
            eventResponseList.add(toEventResponse(event));
        }
        return eventResponseList;
    }
}