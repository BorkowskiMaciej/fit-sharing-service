package com.fitsharingapp.application.common.filter;

import com.fitsharingapp.EnableIntegrationContext;
import com.fitsharingapp.TestDataProvider;
import com.fitsharingapp.domain.user.User;
import com.fitsharingapp.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.fitsharingapp.common.Constants.*;
import static com.fitsharingapp.common.ErrorCode.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableIntegrationContext
public class DeviceInHeaderValidationFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataProvider testDataProvider;

    @Autowired
    private JwtService jwtService;

    @Test
    void should_ReturnBadRequest_When_SendRequestWithoutDeviceIdHeader() {
        User user = testDataProvider.createAndSaveRandomUser();
        String authHeader = "Bearer " + jwtService.generateToken(user);

        webTestClient.get()
                .uri("/keys/my")
                .header(AUTHORIZATION_HEADER, authHeader)
                .header(FS_USER_ID_HEADER, user.getFsUserId().toString())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(MISSING_HEADER.getCode())
                .jsonPath("$.message").isEqualTo(MISSING_HEADER.getMessage()
                        .formatted(FS_DEVICE_ID_HEADER));
    }

    @Test
    void should_ReturnBadRequest_When_SendRequestWithEmptyDeviceIdHeader() {
        User user = testDataProvider.createAndSaveRandomUser();
        String authHeader = "Bearer " + jwtService.generateToken(user);

        webTestClient.get()
                .uri("/keys/my")
                .header(AUTHORIZATION_HEADER, authHeader)
                .header(FS_USER_ID_HEADER, user.getFsUserId().toString())
                .header(FS_DEVICE_ID_HEADER, "")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(MISSING_HEADER.getCode())
                .jsonPath("$.message").isEqualTo(MISSING_HEADER.getMessage()
                        .formatted(FS_DEVICE_ID_HEADER));
    }

    @Test
    void should_ReturnBadRequest_When_SendRequestWithInvalidUUIDInHeader() {
        User user = testDataProvider.createAndSaveRandomUser();
        String authHeader = "Bearer " + jwtService.generateToken(user);

        webTestClient.get()
                .uri("/keys/my")
                .header(AUTHORIZATION_HEADER, authHeader)
                .header(FS_USER_ID_HEADER, user.getFsUserId().toString())
                .header(FS_DEVICE_ID_HEADER, "invalid UUID")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_UUID_IN_HEADER.getCode())
                .jsonPath("$.message").isEqualTo(INVALID_UUID_IN_HEADER.getMessage()
                        .formatted(FS_DEVICE_ID_HEADER));
    }

}
