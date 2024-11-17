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
import static java.util.UUID.randomUUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableIntegrationContext
public class JwtAuthenticationFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataProvider testDataProvider;

    @Autowired
    private JwtService jwtService;

    @Test
    void should_ReturnBadRequest_When_SendRequestWithoutAuthorizationHeader() {
        webTestClient.get()
                .uri("/users/me")
                .header(FS_USER_ID_HEADER, randomUUID().toString())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(MISSING_HEADER.getCode())
                .jsonPath("$.message").isEqualTo(MISSING_HEADER.getMessage()
                        .formatted(AUTHORIZATION_HEADER));
    }

    @Test
    void should_ReturnBadRequest_When_SendRequestWithAuthorizationHeaderWithoutBearerPrefix() {
        webTestClient.get()
                .uri("/users/me")
                .header(AUTHORIZATION_HEADER, "Header without prefix")
                .header(FS_USER_ID_HEADER, randomUUID().toString())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_AUTHORIZATION_HEADER.getCode());
    }

    @Test
    void should_ReturnUnauthorized_When_TokenIsExpired() {
        User user = testDataProvider.createAndSaveRandomUser();
        String expiredToken = jwtService.generateToken(user, 1L);

        webTestClient.get()
                .uri("/users/me")
                .header(AUTHORIZATION_HEADER, "Bearer " + expiredToken)
                .header(FS_USER_ID_HEADER, randomUUID().toString())
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_TOKEN_EXPIRED.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_TokenIsInvalid() {
        webTestClient.get()
                .uri("/users/me")
                .header(AUTHORIZATION_HEADER, "Bearer " + "invalid token")
                .header(FS_USER_ID_HEADER, randomUUID().toString())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_TOKEN.getCode());
    }


}
