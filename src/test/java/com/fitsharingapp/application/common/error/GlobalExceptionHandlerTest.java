package com.fitsharingapp.application.common.error;

import com.fitsharingapp.EnableIntegrationContext;
import com.fitsharingapp.TestDataProvider;
import com.fitsharingapp.TestUserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static com.fitsharingapp.common.Constants.AUTHORIZATION_HEADER;
import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;
import static com.fitsharingapp.common.ErrorCode.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableIntegrationContext
public class GlobalExceptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataProvider testDataProvider;

    private TestUserData testUser;

    @BeforeEach
    void setUp() {
        testUser = testDataProvider.createTestUserData();
    }

    @Test
    void should_ReturnNotFound_When_GetNotExistingResource() {
        webTestClient.get()
                .uri("/not-existing-resource")
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(RESOURCE_NOT_FOUND.getCode())
                .jsonPath("$.message").isEqualTo(RESOURCE_NOT_FOUND.getMessage()
                        .formatted("not-existing-resource"));
    }

    @Test
    void should_ReturnBadRequest_When_InvalidUUIDInPath() {
        webTestClient.get()
                .uri("/users/invalid-uuid")
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_UUID_IN_PATH.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_InvalidContentType() {

        webTestClient.put()
                .uri("/users")
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("""
                            {
                            "username": "username",
                            "email": "email"
                            }
                            """)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_CONTENT_TYPE.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_InvalidData() {

        webTestClient.put()
                .uri("/users")
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("invalid data"))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_DATA.getCode());
    }

}
