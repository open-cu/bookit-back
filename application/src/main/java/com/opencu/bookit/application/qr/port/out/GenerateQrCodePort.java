package com.opencu.bookit.application.qr.port.out;

import com.opencu.bookit.domain.model.user.User;

public interface GenerateQrCodePort {
    byte[] generateUserQrCode(User user);
}
