package com.opencu.bookit.adapter.out.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.opencu.bookit.application.port.out.qr.GenerateQrCodePort;
import com.opencu.bookit.domain.model.user.UserModel;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class QrCodeGenerationAdapter implements GenerateQrCodePort {
    public byte[] generateUserQrCode(UserModel userModel) {
        String userData = String.format(
                "USER:%s:%s %s:%s",
                userModel.getId(),
                userModel.getTgId(),
                userModel.getFirstName(),
                userModel.getLastName() != null ? userModel.getLastName() : ""
                                       );

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 0);

            BitMatrix bitMatrix = qrCodeWriter.encode(
                    userData,
                    BarcodeFormat.QR_CODE,
                    200, 200,
                    hints);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
}
