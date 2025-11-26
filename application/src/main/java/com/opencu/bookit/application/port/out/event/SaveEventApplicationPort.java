package com.opencu.bookit.application.port.out.event;

import com.opencu.bookit.domain.model.event.EventApplicationModel;
import com.opencu.bookit.domain.model.event.EventApplicationStatus;

import java.util.UUID;

public interface SaveEventApplicationPort {
    EventApplicationModel save(EventApplicationModel eventApplication);
    void changeStatusById(UUID id, EventApplicationStatus status);
}