package com.opencu.bookit.domain.model.event;

import com.opencu.bookit.domain.model.area.AreaModel;
import com.opencu.bookit.domain.model.booking.BookingModel;
import com.opencu.bookit.domain.model.contentcategory.*;
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
    private String shortDescription;
    private String fullDescription;
    private Set<ThemeTags> tags = new HashSet<>();
    private Set<ContentFormat> formats = new HashSet<>();
    private Set<ContentTime> times = new HashSet<>();
    private Set<ParticipationFormat> participationFormats = new HashSet<>();
    private Set<TargetAudience> targetAudiences = new HashSet<>();
    private List<String> keys;
    /**
     * stands for start of the event
     */
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int available_places;
    private Set<UserModel> userModels = new HashSet<>();
    private AreaModel areaModel;
    private BookingModel systemBooking;
    private boolean requiresApplication;
    private LocalDateTime registrationDeadline;
}