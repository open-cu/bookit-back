package com.opencu.bookit.adapter.out.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.opencu.bookit.domain.model.user.UserModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QrCodeGenerationAdapterTest {
    @Test
    void generateUserQrCode_ShouldUseCorrectParameters() throws Exception {
        // Arrange
        UserModel testUser = new  UserModel();
        testUser.setFirstName("testFirstName");
        testUser.setLastName("testLastName");
        testUser.setTgId(123L);

        byte[] expectedBytes = new byte[]{1, 2, 3, 4, 5};

        try (MockedConstruction<QRCodeWriter> mockedQrCodeWriter = mockConstruction(QRCodeWriter.class,
                (mock, context) -> {
                    BitMatrix mockBitMatrix = mock(BitMatrix.class);
                    when(mock.encode(anyString(), eq(BarcodeFormat.QR_CODE), eq(200), eq(200), anyMap()))
                            .thenReturn(mockBitMatrix);
                });
             MockedStatic<MatrixToImageWriter> mockedImageWriter = mockStatic(MatrixToImageWriter.class)) {

            mockedImageWriter.when(() ->
                    MatrixToImageWriter.writeToStream(any(BitMatrix.class), eq("PNG"), any(ByteArrayOutputStream.class))
            ).thenAnswer(invocation -> {
                ByteArrayOutputStream stream = invocation.getArgument(2);
                stream.write(expectedBytes);
                return null;
            });

            QrCodeGenerationAdapter adapter = new QrCodeGenerationAdapter();

            // Act
            byte[] result = adapter.generateUserQrCode(testUser);

            // Assert
            assertArrayEquals(expectedBytes, result);
        }
    }

    @Test
    void generateUserQrCode_ShouldFormatUserDataCorrectly() throws Exception {
        // Arrange
        UserModel testUser = new  UserModel();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setTgId(123L);

        String expectedData = "USER:123:tg123:John:Doe";

        try (MockedConstruction<QRCodeWriter> mockedQrCodeWriter = mockConstruction(QRCodeWriter.class,
                (mock, context) -> {
                    BitMatrix mockBitMatrix = mock(BitMatrix.class);
                    when(mock.encode(eq(expectedData), eq(BarcodeFormat.QR_CODE), eq(200), eq(200), anyMap()))
                            .thenReturn(mockBitMatrix);
                });
             MockedStatic<MatrixToImageWriter> mockedImageWriter = mockStatic(MatrixToImageWriter.class)) {

            mockedImageWriter.when(() ->
                    MatrixToImageWriter.writeToStream(any(BitMatrix.class), eq("PNG"), any(ByteArrayOutputStream.class))
            ).thenAnswer(invocation -> null);

            QrCodeGenerationAdapter adapter = new QrCodeGenerationAdapter();

            // Act
            adapter.generateUserQrCode(testUser);

        }
    }
}