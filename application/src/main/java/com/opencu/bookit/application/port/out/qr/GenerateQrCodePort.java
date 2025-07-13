package com.opencu.bookit.application.port.out.qr;

import com.opencu.bookit.domain.model.user.UserModel;

public interface GenerateQrCodePort {
    byte[] generateUserQrCode(UserModel userModel);
}
