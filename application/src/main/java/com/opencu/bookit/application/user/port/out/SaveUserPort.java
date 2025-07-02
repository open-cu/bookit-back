package com.opencu.bookit.application.user.port.out;

import com.opencu.bookit.domain.model.user.User;

public interface SaveUserPort {
    User save(User user);
}
