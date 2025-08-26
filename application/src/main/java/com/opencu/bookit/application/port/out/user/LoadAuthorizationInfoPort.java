package com.opencu.bookit.application.port.out.user;

import com.opencu.bookit.domain.model.user.UserModel;

public interface LoadAuthorizationInfoPort {
    UserModel getCurrentUser();
}
