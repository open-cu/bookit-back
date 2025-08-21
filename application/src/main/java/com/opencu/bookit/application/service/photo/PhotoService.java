package com.opencu.bookit.application.service.photo;

import com.opencu.bookit.application.port.out.s3.LoadS3Port;
import com.opencu.bookit.application.port.out.s3.SaveS3Port;
import com.opencu.bookit.domain.model.image.ImageModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PhotoService {
    private final LoadS3Port loadS3Port;
    private final SaveS3Port saveS3Port;

    public PhotoService(LoadS3Port loadS3Port, SaveS3Port saveS3Port) {
        this.loadS3Port = loadS3Port;
        this.saveS3Port = saveS3Port;
    }

    public List<ImageModel> getImagesFromKeys(List<String> keys, Boolean sendPhotos) throws IOException {
        return loadS3Port.getImagesFromKeys(keys, sendPhotos);
    }

    public List<String> upload(List<MultipartFile> photos) throws IOException {
        return saveS3Port.upload(photos);
    }
}
