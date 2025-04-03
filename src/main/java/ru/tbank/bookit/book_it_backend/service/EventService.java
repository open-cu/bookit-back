package ru.tbank.bookit.book_it_backend.service;

import org.springframework.stereotype.Service;
import ru.tbank.bookit.book_it_backend.model.Event;
import ru.tbank.bookit.book_it_backend.model.NewsTag;
import ru.tbank.bookit.book_it_backend.repository.EventRepository;

import java.util.List;
import java.util.Set;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public List<Event> findByTags(Set<NewsTag> tags){
        return eventRepository.findByTagsIn(tags);
    }

}
