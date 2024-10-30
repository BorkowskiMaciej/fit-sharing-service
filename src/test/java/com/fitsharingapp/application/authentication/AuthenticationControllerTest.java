package com.fitsharingapp.application.authentication;

import com.fitsharingapp.EnableIntegrationContext;
import com.fitsharingapp.TestDataProvider;
import com.fitsharingapp.application.authentication.dto.*;
import com.fitsharingapp.application.key.PublicKeyService;
import com.fitsharingapp.domain.key.PublicKey;
import com.fitsharingapp.domain.user.User;
import com.fitsharingapp.domain.user.UserRepository;
import com.fitsharingapp.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

import static com.fitsharingapp.common.Constants.AUTHORIZATION_HEADER;
import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;
import static com.fitsharingapp.common.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableIntegrationContext
public class AuthenticationControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataProvider testDataProvider;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PublicKeyService publicKeyService;

    @Test
    void should_RegisterUser_When_Requested() {
        RegisterRequest request = testDataProvider.createRandomRegisterRequest();

        RegisterResponse response = webTestClient
                .post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(RegisterResponse.class)
                .returnResult()
                .getResponseBody();

        Optional<User> savedUser = userRepository.findById(response.fsUserId());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getUsername()).isEqualTo(request.username());

        Optional<PublicKey> key = publicKeyService.getPublicKey(response.fsUserId(), request.deviceId());
        assertThat(key).isPresent();
        assertThat(request.publicKey()).isEqualTo(key.get().getKey());
    }

    @Test
    void should_LoginUser_When_Requested() {
        User registeredUser = testDataProvider.createAndSaveRandomUser();
        LoginRequest request = new LoginRequest(registeredUser.getUsername(), "password");

        LoginResponse loginResponse = webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(LoginResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(loginResponse.fsUserId()).isEqualTo(registeredUser.getFsUserId());

        webTestClient.get()
                .uri("/users/me")
                .header(AUTHORIZATION_HEADER, "Bearer " + loginResponse.token())
                .header(FS_USER_ID_HEADER, loginResponse.fsUserId().toString())
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void should_SendResetPasswordToken_When_Requested() {
        User registeredUser = testDataProvider.createAndSaveRandomUser();
        ResetPasswordRequest request = new ResetPasswordRequest(registeredUser.getEmail());

        webTestClient.post()
                .uri("/auth/reset-password-request")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void should_ResetPassword_When_Requested() {
        User registeredUser = testDataProvider.createAndSaveRandomUser();
        String resetPasswordToken = jwtService.generateResetPasswordToken(registeredUser);

        ResetPasswordDataRequest resetPasswordRequest = new ResetPasswordDataRequest(
                registeredUser.getEmail(), resetPasswordToken, "newpassword");

        webTestClient.post()
                .uri("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resetPasswordRequest)
                .exchange()
                .expectStatus()
                .isOk();

        LoginRequest loginRequest = new LoginRequest(registeredUser.getUsername(), "newpassword");

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void should_ReturnBadRequest_When_SendInvalidRegisterRequest() {
        RegisterRequest request = testDataProvider.createRandomRegisterRequest()
                .toBuilder()
                .username("")
                .build();

        webTestClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_DATA_FOR_FIELD.getCode())
                .jsonPath("$.message").isEqualTo(INVALID_DATA_FOR_FIELD.getMessage()
                        .formatted("username", "Username must not be blank"));

    }

    @Test
    void should_ReturnBadRequest_When_SendRegisterRequestAndUsernameOrEmailAlreadyExists() {
        User savedUser = testDataProvider.createAndSaveRandomUser();
        RegisterRequest request = testDataProvider.createRandomRegisterRequest()
                .toBuilder()
                .username(savedUser.getUsername())
                .build();
        RegisterRequest request2 = testDataProvider.createRandomRegisterRequest()
                .toBuilder()
                .email(savedUser.getEmail())
                .build();

        webTestClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(NOT_UNIQUE_USERNAME.getCode());

        webTestClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request2)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(NOT_UNIQUE_EMAIL.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_InvalidLoginRequest() {
        LoginRequest request = new LoginRequest(null, "password");

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_DATA_FOR_FIELD.getCode())
                .jsonPath("$.message").isEqualTo(INVALID_DATA_FOR_FIELD.getMessage()
                        .formatted("username", "Username must not be null"));

    }

    @Test
    void should_ReturnUnauthorized_When_LoginRequestWithBadCredentials() {
        LoginRequest request = new LoginRequest("", "password");

        webTestClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .jsonPath("$.code").isEqualTo(BAD_CREDENTIALS.getCode());

    }

    @Test
    void should_ReturnNotFound_When_ResetPasswordRequestWithInvalidEmail() {
        ResetPasswordRequest request = new ResetPasswordRequest("nonexistent@example.com");

        webTestClient.post()
                .uri("/auth/reset-password-request")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(USER_NOT_FOUND.getCode());
    }

    @Test
    void should_ReturnNotFound_When_ResetPasswordWithInvalidEmail() {
        ResetPasswordDataRequest resetPasswordRequest = new ResetPasswordDataRequest("nonexistent@example.com", "someCode", "newPassword");

        webTestClient.post()
                .uri("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resetPasswordRequest)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(USER_NOT_FOUND.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_ResetPasswordWithInvalidToken() {
        User registeredUser = testDataProvider.createAndSaveRandomUser();
        String invalidToken = "invalidToken";

        ResetPasswordDataRequest resetPasswordRequest = new ResetPasswordDataRequest(
                registeredUser.getEmail(), invalidToken, "newPassword");

        webTestClient.post()
                .uri("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resetPasswordRequest)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_RESET_PASSWORD_CODE.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_ResetPasswordWithInvalidData() {
        User registeredUser = testDataProvider.createAndSaveRandomUser();
        String resetPasswordToken = jwtService.generateResetPasswordToken(registeredUser);

        ResetPasswordDataRequest resetPasswordRequest = new ResetPasswordDataRequest(
                registeredUser.getEmail(), resetPasswordToken, "");

        webTestClient.post()
                .uri("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resetPasswordRequest)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_DATA_FOR_FIELD.getCode())
                .jsonPath("$.message").isEqualTo(INVALID_DATA_FOR_FIELD.getMessage()
                        .formatted("newPassword", "New password must not be blank"));
    }

    @Test
    void should_ReturnBadRequest_When_ResetPasswordWithExpiredToken() {
        User registeredUser = testDataProvider.createAndSaveRandomUser();
        String expiredToken = jwtService.generateToken(registeredUser, 1L);

        ResetPasswordDataRequest resetPasswordRequest = new ResetPasswordDataRequest(
                registeredUser.getEmail(), expiredToken, "newPassword");

        webTestClient.post()
                .uri("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(resetPasswordRequest)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(INVALID_RESET_PASSWORD_CODE.getCode());
    }

}
