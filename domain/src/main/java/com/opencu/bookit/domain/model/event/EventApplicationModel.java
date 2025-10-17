package com.opencu.bookit.domain.model.event;

import com.opencu.bookit.domain.model.user.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventApplicationModel {
    private UUID id;
    private UserModel userModel;
    private EventModel eventModel;
    private String cityOfResidence;
    private LocalDate dateOfBirth;
    private String activityDetails;
}