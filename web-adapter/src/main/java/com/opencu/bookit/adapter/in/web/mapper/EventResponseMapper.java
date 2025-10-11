package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.EventResponse;
import com.opencu.bookit.application.service.photo.PhotoService;
import com.opencu.bookit.domain.model.event.EventModel;
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

    public EventResponse toEventResponse(EventModel event, Boolean sendPhotos) throws IOException {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getShortDescription().get(),
                event.getFullDescription(),
                event.getTags(),
                event.getFormats(),
                event.getTimes(),
                event.getParticipationFormats(),
                event.getTargetAudiences(),
                service.getImagesFromKeys(event.getKeys(), sendPhotos),
                event.getStartTime(),
                event.getEndTime(),
                event.getAvailable_places()
        );
    }

    public List<EventResponse> toEventResponseList(List<EventModel> events, Boolean sendPhotos) throws IOException {
        List<EventResponse> eventResponseList = new ArrayList<>();
        for (var event:  events) {
            eventResponseList.add(toEventResponse(event, sendPhotos));
        }
        return eventResponseList;
    }
}