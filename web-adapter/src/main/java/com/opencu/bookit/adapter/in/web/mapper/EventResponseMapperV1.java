package com.opencu.bookit.adapter.in.web.mapper;

import com.opencu.bookit.adapter.in.web.dto.response.EventResponse;
import com.opencu.bookit.adapter.in.web.dto.response.EventResponseV1;
import com.opencu.bookit.application.service.photo.PhotoService;
import com.opencu.bookit.domain.model.event.EventModel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventResponseMapperV1 {
    private final PhotoService service;

    public EventResponseMapperV1(PhotoService service) {
        this.service = service;
    }

    public EventResponseV1 toEventResponse(EventModel event) throws IOException {
        return new EventResponseV1(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTags(),
                event.getFormats(),
                event.getTimes(),
                event.getParticipationFormats(),
                service.getImagesFromKeys(event.getKeys()),
                event.getStartTime(),
                event.getEndTime(),
                event.getAvailable_places()
        );
    }

    public List<EventResponseV1> toEventResponseList(List<EventModel> events) throws IOException {
        List<EventResponseV1> eventResponseList = new ArrayList<>();
        for (var event:  events) {
            eventResponseList.add(toEventResponse(event));
        }
        return eventResponseList;
    }
}
