package com.opencu.bookit.domain.model.event;

import com.opencu.bookit.domain.model.user.UserModel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventModel {
    private UUID id;
    private String name;
    private String description;
    private Set<ThemeTags> tags = new HashSet<>();
    private LocalDateTime date;
    private int available_places;
    private Set<UserModel> userModels = new HashSet<>();
}