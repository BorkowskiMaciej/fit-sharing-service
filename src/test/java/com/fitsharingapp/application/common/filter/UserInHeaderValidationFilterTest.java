package com.fitsharingapp.application.common.filter;

import com.fitsharingapp.EnableIntegrationContext;
import com.fitsharingapp.TestDataProvider;
import com.fitsharingapp.domain.user.User;
import com.fitsharingapp.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.fitsharingapp.common.Constants.AUTHORIZATION_HEADER;
import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;
import static com.fitsharingapp.common.ErrorCode.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableIntegrationContext
public class UserInHeaderValidationFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataProvider testDataProvider;

    @Autowired
    private JwtService jwtService;

    @Test
    void should_ReturnBadRequest_When_SendRequestWithoutFsUserIdHeader() {
        User user = testDataProvider.createAndSaveRandomUser();
        String authHeader = "Bearer " + jwtService.generateToken(user);

        webTestClient.get()
                .uri("/users/me")
                .header(AUTHORIZATION_HEADER, authHeader)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(MISSING_HEADER.getCode())
                .jsonPath("$.message").isEqualTo(MISSING_HEADER.getMessage()
                        .formatted(FS_USER_ID_HEADER));
    }

    @Test
    void should_ReturnBadRequest_When_SendRequestWithEmptyFsUserIdHeader() {
        User user = testDataProvider.createAndSaveRandomUser();
        String authHeader = "Bearer " + jwtService.generateToken(user);

        webTestClient.get()
                .uri("/users/me")
                .header(AUTHORIZATION_HEADER, authHeader)
                .header(FS_USER_ID_HEADER, "")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(MISSING_HEADER.getCode())
                .jsonPath("$.message").isEqualTo(MISSING_HEADER.getMessage()
                        .formatted(FS_USER_ID_HEADER));
    }

    @Test
    void should_ReturnForbidden_When_SendRequestWithNotAuthenticatedUserInHeader() {
        User user = testDataProvider.createAndSaveRandomUser();
        User notAuthenticatedUser = testDataProvider.createAndSaveRandomUser();
        String authHeader = "Bearer " + jwtService.generateToken(user);

        webTestClient.get()
                .uri("/users/me")
                .header(AUTHORIZATION_HEADER, authHeader)
                .header(FS_USER_ID_HEADER, notAuthenticatedUser.getFsUserId().toString())
                .exchange()
                .expectStatus()
                .isForbidden()
                .expectBody()
                .jsonPath("$.code").isEqualTo(NOT_AUTHENTICATED_USER_IN_HEADER.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_SendRequestWithInvalidUUIDInHeader() {
        User user = testDataProvider.createAndSaveRandomUser();
        String authHeader = "Bearer " + jwtService.generateToken(user);

        webTestClient.get()
                .uri("/users/me")
                .header(AUTHORIZATION_HEADER, authHeader)
                .header(FS_USER_ID_HEADER, "invalid UUID")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_UUID_IN_HEADER.getCode())
                .jsonPath("$.message").isEqualTo(INVALID_UUID_IN_HEADER.getMessage()
                        .formatted(FS_USER_ID_HEADER));
    }

}
