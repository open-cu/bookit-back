package com.opencu.bookit.application.port.out.event;

import java.util.UUID;

public interface DeleteEventApplicationPort {
    void deleteById(UUID id);
}
