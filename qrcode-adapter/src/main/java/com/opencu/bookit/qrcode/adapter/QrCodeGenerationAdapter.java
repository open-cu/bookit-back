package com.opencu.bookit.qrcode.adapter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.opencu.bookit.domain.model.user.User;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class QrCodeGenerationAdapter {
    public byte[] generateUserQrCode(User user) {
        String userData = String.format(
                "USER:%s:%s %s:%s",
                user.getId(),
                user.getTgId(),
                user.getFirstName(),
                user.getLastName() != null ? user.getLastName() : ""
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
