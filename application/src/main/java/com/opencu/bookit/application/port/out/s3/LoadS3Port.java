package com.opencu.bookit.application.port.out.s3;

import com.opencu.bookit.domain.model.image.ImageModel;

import java.io.IOException;
import java.util.List;

public interface LoadS3Port {
    List<ImageModel> getImagesFromKeys(List<String> keys, Boolean sendPhotos) throws IOException;
}
