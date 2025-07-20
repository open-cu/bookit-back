package com.opencu.bookit.application.port.out.user;

import java.util.UUID;

public interface DeleteUserPort {
    void deleteById(UUID userId);
}
