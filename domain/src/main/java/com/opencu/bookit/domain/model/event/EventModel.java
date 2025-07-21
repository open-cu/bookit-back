package com.opencu.bookit.domain.model.event;

import com.opencu.bookit.domain.model.contentcategory.ContentFormat;
import com.opencu.bookit.domain.model.contentcategory.ContentTime;
import com.opencu.bookit.domain.model.contentcategory.ParticipationFormat;
import com.opencu.bookit.domain.model.contentcategory.ThemeTags;
import com.opencu.bookit.domain.model.user.UserModel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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
    private Set<ContentFormat> formats = new HashSet<>();
    private Set<ContentTime> times = new HashSet<>();
    private Set<ParticipationFormat> participationFormats = new HashSet<>();
    private List<String> keys;
    private LocalDateTime date;
    private int available_places;
    private Set<UserModel> userModels = new HashSet<>();
}