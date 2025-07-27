package com.opencu.bookit.application.port.out.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SaveS3Port {
    /**
     * @param photos list of photos related to some object
     * @return list of so-called "keys" (i.e. names of files in s3 storage)
     */
    List<String> upload(List<MultipartFile> photos) throws IOException;
}
