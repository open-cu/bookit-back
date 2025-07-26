package com.opencu.bookit.adapter.out.s3_adapter;

import com.opencu.bookit.application.port.out.s3.LoadS3Port;
import com.opencu.bookit.application.port.out.s3.SaveS3Port;
import com.opencu.bookit.domain.model.image.ImageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class S3Adapter implements LoadS3Port, SaveS3Port {
    private final S3Client s3Client;

    private final String KEY_ID;
    private final String SECRET_KEY;
    private final String REGION;
    private final String S3_ENDPOINT;
    private final String BUCKET;

    public S3Adapter(
            @Value("${yandex-cloud.key-id}") String keyId,
            @Value("${yandex-cloud.secret-key}") String secretKey,
            @Value("${yandex-cloud.region}") String region,
            @Value("${yandex-cloud.s3-endpoint}") String s3Endpoint,
            @Value("${yandex-cloud.bucket}") String bucket
    ) {
        KEY_ID = keyId;
        SECRET_KEY = secretKey;
        REGION = region;
        S3_ENDPOINT = s3Endpoint;
        BUCKET = bucket;

        AwsCredentials credentials = AwsBasicCredentials.create(KEY_ID, SECRET_KEY);
        s3Client = S3Client.builder()
                .httpClient(ApacheHttpClient.create())
                .region(Region.of(REGION))
                .endpointOverride(URI.create(S3_ENDPOINT))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /**
     * @param keys list of names of files in s3 storage
     * @return list of photos related to some object (e.g. NewsResponse)
     */
    @Override
    public List<ImageModel> getImagesFromKeys(List<String> keys) throws IOException {
        List<ImageModel> imageModels = new ArrayList<>();
        for (String key : keys) {
            imageModels.add(getImageFromKey(key));
        }
        return imageModels;
    }

    /**
     * @param photos list of photos related to some object 
     * @return list of so-called "keys" (i.e. names of files in s3 storage)
     */
    @Override
    public List<String> upload(List<MultipartFile> photos) throws IOException {
        List<String> keys = new ArrayList<>();
        for (MultipartFile photo : photos) {
            String key = photo.getOriginalFilename();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(key)
                    .contentType(photo.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(photo.getBytes()));
            keys.add(key);
        }
        return keys;
    }

    private ImageModel getImageFromKey(String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest)) {
            byte[] bytes = responseInputStream.readAllBytes();
            GetObjectResponse metadata = responseInputStream.response();

            String base64Data = Base64.getEncoder().encodeToString(bytes);

            return new ImageModel(
                    key,
                    metadata.contentType(),
                    metadata.contentLength(),
                    metadata.eTag(),
                    metadata.lastModified().toString(),
                    base64Data
            );
        }
    }
}
