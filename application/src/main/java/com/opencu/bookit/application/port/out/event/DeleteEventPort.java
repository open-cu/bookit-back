package com.opencu.bookit.application.port.out.event;

import java.util.UUID;

public interface DeleteEventPort {
    void delete(UUID eventId);
}
