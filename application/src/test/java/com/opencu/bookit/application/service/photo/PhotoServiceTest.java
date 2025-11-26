package com.opencu.bookit.application.service.photo;

import com.opencu.bookit.application.port.out.s3.LoadS3Port;
import com.opencu.bookit.application.port.out.s3.SaveS3Port;
import com.opencu.bookit.domain.model.image.ImageModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock private LoadS3Port loadS3Port;
    @Mock private SaveS3Port saveS3Port;

    @InjectMocks private PhotoService service;

    @Test
    @DisplayName("getImagesFromKeys delegates to LoadS3Port and returns images")
    void getImagesFromKeys_ok() throws IOException {
        List<String> keys = List.of("k1", "k2");
        ImageModel img1 = new ImageModel("k1", "image/png", 100, "etag1", "2025-01-01T00:00:00Z", "base64data1");
        ImageModel img2 = new ImageModel("k2", "image/jpeg", 200, "etag2", "2025-01-02T00:00:00Z", "base64data2");
        when(loadS3Port.getImagesFromKeys(keys, true)).thenReturn(List.of(img1, img2));

        List<ImageModel> res = service.getImagesFromKeys(keys, true);
        assertEquals(2, res.size());
        verify(loadS3Port).getImagesFromKeys(keys, true);
        verifyNoMoreInteractions(loadS3Port);
        verifyNoInteractions(saveS3Port);
    }

    @Test
    @DisplayName("getImagesFromKeys propagates IOException")
    void getImagesFromKeys_throws() throws IOException {
        List<String> keys = List.of("k1");
        when(loadS3Port.getImagesFromKeys(keys, false)).thenThrow(new IOException("failed"));
        IOException ex = assertThrows(IOException.class, () -> service.getImagesFromKeys(keys, false));
        assertTrue(ex.getMessage().contains("failed"));
        verify(loadS3Port).getImagesFromKeys(keys, false);
    }

    @Test
    @DisplayName("upload delegates to SaveS3Port and returns keys")
    void upload_ok() throws IOException {
        MultipartFile f1 = mock(MultipartFile.class);
        MultipartFile f2 = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(f1, f2);
        when(saveS3Port.upload(files)).thenReturn(List.of("k1", "k2"));

        List<String> res = service.upload(files);
        assertEquals(List.of("k1", "k2"), res);
        verify(saveS3Port).upload(files);
        verifyNoMoreInteractions(saveS3Port);
        verifyNoInteractions(loadS3Port);
    }

    @Test
    @DisplayName("upload propagates IOException")
    void upload_throws() throws IOException {
        MultipartFile f1 = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(f1);
        when(saveS3Port.upload(files)).thenThrow(new IOException("s3 error"));

        IOException ex = assertThrows(IOException.class, () -> service.upload(files));
        assertTrue(ex.getMessage().contains("s3 error"));
        verify(saveS3Port).upload(files);
    }
}
