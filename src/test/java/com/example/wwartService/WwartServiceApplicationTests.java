package com.example.wwartService;

import com.example.wwartService.service.GoogleCloudStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class WwartServiceApplicationTests {
    @MockBean
    private GoogleCloudStorageService googleCloudStorageService;

    @Test
    void contextLoads() {
    }

}
