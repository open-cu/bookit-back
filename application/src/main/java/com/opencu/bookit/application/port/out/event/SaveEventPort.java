package com.opencu.bookit.application.port.out.event;

import com.opencu.bookit.domain.model.event.EventModel;

public interface SaveEventPort {
    void save(EventModel eventModel);
}
