package com.example.wwartService.config;

import com.example.wwartService.service.GoogleCloudStorageService;
import com.google.cloud.storage.Storage;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class DummyStorageConfig {
    @Bean
    public GoogleCloudStorageService googleCloudStorageService() {
        final Storage storageMock = Mockito.mock(Storage.class);
        final String dummyBucketName = "dummy-bucket";

        return new GoogleCloudStorageService(storageMock, dummyBucketName);
    }
}
