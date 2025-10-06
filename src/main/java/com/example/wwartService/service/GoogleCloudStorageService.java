package com.example.wwartService.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class GoogleCloudStorageService {

    private final Storage storage;
    private final String bucketName;

    public GoogleCloudStorageService(final Storage storage,
                                     @Value("${spring.cloud.gcp.storage.bucket-name}") final String bucketName) {
        this.storage = storage;
        this.bucketName = bucketName;
    }

    public String uploadFile(final MultipartFile file) throws IOException {
        final String fileName = "comments_" + UUID.randomUUID() + ".png";

        final BlobId blobId = BlobId.of(bucketName, "uploads/" + fileName);
        final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        storage.create(blobInfo, file.getBytes());

        return String.format("https://storage.googleapis.com/%s/uploads/%s", bucketName, fileName);
    }
}
