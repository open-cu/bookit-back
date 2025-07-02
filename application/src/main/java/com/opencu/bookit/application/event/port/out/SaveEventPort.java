package com.opencu.bookit.application.event.port.out;

import com.opencu.bookit.domain.model.event.Event;

public interface SaveEventPort {
    void save(Event event);
}
