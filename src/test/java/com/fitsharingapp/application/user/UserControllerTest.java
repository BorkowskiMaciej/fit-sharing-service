package com.fitsharingapp.application.user;

import com.fitsharingapp.EnableIntegrationContext;
import com.fitsharingapp.TestDataProvider;
import com.fitsharingapp.TestUserData;
import com.fitsharingapp.application.authentication.dto.LoginRequest;
import com.fitsharingapp.application.key.PublicKeyService;
import com.fitsharingapp.application.news.NewsService;
import com.fitsharingapp.application.news.dto.CreateNewsRequest;
import com.fitsharingapp.application.news.dto.CreateReferenceNewsRequest;
import com.fitsharingapp.application.relationship.RelationshipService;
import com.fitsharingapp.application.user.dto.UpdatePasswordRequest;
import com.fitsharingapp.application.user.dto.UpdateUserRequest;
import com.fitsharingapp.application.user.dto.UserResponse;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.news.ReferenceNews;
import com.fitsharingapp.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static com.fitsharingapp.common.Constants.AUTHORIZATION_HEADER;
import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;
import static com.fitsharingapp.common.ErrorCode.*;
import static com.fitsharingapp.domain.relationship.RelationshipStatus.ACCEPTED;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableIntegrationContext
public class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataProvider testDataProvider;

    @Autowired
    private PublicKeyService publicKeyService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private UserService userService;

    @Autowired
    private NewsService newsService;

    private TestUserData testUser;

    @BeforeEach
    void setUp() {
        testUser = testDataProvider.createTestUserData();
    }

    @Test
    void should_UpdateUser_When_Requested() {
        UpdateUserRequest request = new UpdateUserRequest(
                "UpdatedName",
                "UpdatedName",
                "Updated description",
                "MALE",
                null
        );

        UserResponse response = webTestClient.put()
                .uri("/users")
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(UserResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.firstName()).isEqualTo(request.firstName());
    }

    @Test
    void should_ReturnBadRequest_When_UpdateUserWithInvalidData() {
        UpdateUserRequest request = new UpdateUserRequest(
                "",
                "UpdatedName",
                "Updated description",
                "MALE",
                null
        );

        webTestClient.put()
                .uri("/users")
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_DATA_FOR_FIELD.getCode())
                .jsonPath("$.message").isEqualTo(INVALID_DATA_FOR_FIELD.getMessage()
                        .formatted("firstName", "First name must not be blank"));

    }

    @Test
    void should_ChangePassword_When_Requested() {
        LoginRequest loginRequest = new LoginRequest(testUser.user().getUsername(), "password");

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus()
                .isOk();

        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "password",
                "newPassword"
        );

        webTestClient.patch()
                .uri("/users/password")
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk();

        LoginRequest newLoginRequest = new LoginRequest(testUser.user().getUsername(), "newPassword");

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newLoginRequest)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void should_ReturnBadRequest_When_ChangePasswordWithInvalidOldOne() {
        LoginRequest loginRequest = new LoginRequest(testUser.user().getUsername(), "password");

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus()
                .isOk();

        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "wrong password",
                "newPassword"
        );

        webTestClient.patch()
                .uri("/users/password")
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_OLD_PASSWORD.getCode());

    }

    @Test
    void should_GetAuthenticatedUser_When_Requested() {
        UserResponse response = webTestClient.get()
                .uri("/users/me")
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(UserResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.fsUserId()).isEqualTo(testUser.user().getFsUserId());
    }

    @Test
    void should_GetUserById_When_Requested() {
        User user = testDataProvider.createAndSaveRandomUser();

        UserResponse response = webTestClient.get()
                .uri("/users/{fsUserId}", user.getFsUserId())
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(UserResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.fsUserId()).isEqualTo(user.getFsUserId());

    }

    @Test
    void should_ReturnNotFound_When_GetUserByIdThatDoesNotExist() {
        UUID nonExistentId = randomUUID();

        webTestClient.get()
                .uri("/users/{fsUserId}", nonExistentId)
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(USER_NOT_FOUND.getCode());
    }

    @Test
    void should_DeleteUser_When_Requested() {
        TestUserData friend = testDataProvider.createTestUserData();
        testDataProvider.createRelationship(testUser.user().getFsUserId(), friend.user().getFsUserId(), ACCEPTED);

        publicKeyService.savePublicKey(testUser.user().getFsUserId(), testUser.deviceId(), "PUBLIC_KEY".getBytes());
        publicKeyService.savePublicKey(friend.user().getFsUserId(), friend.deviceId(), "PUBLIC_KEY".getBytes());

        CreateReferenceNewsRequest referenceNewsRequest = new CreateReferenceNewsRequest("Data");
        ReferenceNews referenceNews = newsService.createReferenceNews(
                testUser.user().getFsUserId(), testUser.deviceId(), referenceNewsRequest);
        CreateNewsRequest newsRequest = new CreateNewsRequest(
                referenceNews.getId(), friend.user().getFsUserId(), friend.deviceId(), "Data");
        newsService.createNews(testUser.user().getFsUserId(), newsRequest);

        assertThat(userService.getUserById(testUser.user().getFsUserId(), USER_NOT_FOUND)).isNotNull();
        Assertions.assertThat(relationshipService.getAcceptedRelationships(testUser.user().getFsUserId())).hasSize(1);
        assertThat(publicKeyService.getPublicKey(testUser.user().getFsUserId(), testUser.deviceId())).isNotNull();
        Assertions.assertThat(newsService.getAllPublishedNews(testUser.user().getFsUserId(), testUser.deviceId())).hasSize(1);
        Assertions.assertThat(newsService.getAllReceivedNews(friend.user().getFsUserId(), friend.deviceId())).hasSize(1);

        webTestClient.delete()
                .uri("/users")
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isNoContent();

        assertThrows(ServiceException.class, () -> userService.getUserById(testUser.user().getFsUserId(), USER_NOT_FOUND));
        Assertions.assertThat(relationshipService.getAcceptedRelationships(testUser.user().getFsUserId())).hasSize(0);
        assertThat(publicKeyService.getPublicKey(testUser.user().getFsUserId(), testUser.deviceId())).isEmpty();
        Assertions.assertThat(newsService.getAllPublishedNews(testUser.user().getFsUserId(), testUser.deviceId())).hasSize(0);
        Assertions.assertThat(newsService.getAllReceivedNews(friend.user().getFsUserId(), friend.deviceId())).hasSize(0);
    }

    @Test
    void should_GetUserBySearchTerm_When_Requested() {
        testDataProvider.createAndSaveRandomUser();

        List<UserResponse> response = webTestClient.get()
                .uri(UriComponentsBuilder.fromUriString("/users/search")
                        .queryParam("searchTerm", "user")
                        .build()
                        .toUriString())
                .header(AUTHORIZATION_HEADER, testUser.authorizationHeader())
                .header(FS_USER_ID_HEADER, testUser.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(UserResponse.class)
                .returnResult()
                .getResponseBody();

        Assertions.assertThat(response).isNotEmpty();
    }


}
