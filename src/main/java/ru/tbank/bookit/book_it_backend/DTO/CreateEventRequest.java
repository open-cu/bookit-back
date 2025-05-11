package ru.tbank.bookit.book_it_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.tbank.bookit.book_it_backend.model.ThemeTags;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter

@AllArgsConstructor
public class CreateEventRequest {
    private String name;
    private String description;
    private Set<ThemeTags> tags;
    private LocalDateTime date;
    private int available_places;
    private String user_list;
}