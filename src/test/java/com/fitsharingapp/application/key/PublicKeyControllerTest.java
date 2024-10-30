package com.fitsharingapp.application.key;

import com.fitsharingapp.EnableIntegrationContext;
import com.fitsharingapp.TestDataProvider;
import com.fitsharingapp.TestUserData;
import com.fitsharingapp.application.key.dto.CreatePublicKeyRequest;
import com.fitsharingapp.application.key.dto.PublicKeyResponse;
import com.fitsharingapp.domain.key.PublicKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

import static com.fitsharingapp.common.Constants.*;
import static com.fitsharingapp.common.ErrorCode.DEVICE_NOT_FOUND;
import static com.fitsharingapp.common.ErrorCode.PUBLIC_KEY_ALREADY_EXISTS;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableIntegrationContext
public class PublicKeyControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataProvider testDataProvider;

    @Autowired
    private PublicKeyService publicKeyService;
    private static final byte[] PUBLIC_KEY = "PUBLIC_KEY".getBytes();

    @Test
    void should_CreatePublicKey_When_Requested() {
        TestUserData userData = testDataProvider.createTestUserData();
        CreatePublicKeyRequest request = new CreatePublicKeyRequest(PUBLIC_KEY, userData.deviceId());

        webTestClient.post()
                .uri("/keys")
                .header(AUTHORIZATION_HEADER, userData.authorizationHeader())
                .header(FS_USER_ID_HEADER, userData.user().getFsUserId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        Optional<PublicKey> publicKey = publicKeyService.getPublicKey(
                userData.user().getFsUserId(), userData.deviceId());
        assertThat(publicKey).isPresent();
        assertThat(publicKey.get().getKey()).isEqualTo(PUBLIC_KEY);
    }

    @Test
    void should_ReturnMyKey_When_Requested() {
        TestUserData userData = testDataProvider.createTestUserData();
        publicKeyService.savePublicKey(userData.user().getFsUserId(), userData.deviceId(), PUBLIC_KEY);

        PublicKeyResponse response = webTestClient
                .get()
                .uri("/keys/my")
                .header(AUTHORIZATION_HEADER, userData.authorizationHeader())
                .header(FS_USER_ID_HEADER, userData.user().getFsUserId().toString())
                .header(FS_DEVICE_ID_HEADER, userData.deviceId().toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PublicKeyResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.publicKey()).isEqualTo(PUBLIC_KEY);
    }

    @Test
    void should_DeleteAllKeysForUser_When_Requested() {
        TestUserData userData = testDataProvider.createTestUserData();
        publicKeyService.savePublicKey(userData.user().getFsUserId(), randomUUID(), PUBLIC_KEY);
        publicKeyService.savePublicKey(userData.user().getFsUserId(), randomUUID(), PUBLIC_KEY);

        assertThat(publicKeyService.getPublicKeys(userData.user().getFsUserId()).size()).isEqualTo(2);

        webTestClient
                .delete()
                .uri("/keys")
                .header(AUTHORIZATION_HEADER, userData.authorizationHeader())
                .header(FS_USER_ID_HEADER, userData.user().getFsUserId().toString())
                .exchange()
                .expectStatus()
                .isOk();

        assertThat(publicKeyService.getPublicKeys(userData.user().getFsUserId())).isEmpty();
    }

    @Test
    void should_ReturnBadRequest_When_KeyAlreadyExists() {
        TestUserData userData = testDataProvider.createTestUserData();
        publicKeyService.savePublicKey(userData.user().getFsUserId(), userData.deviceId(), PUBLIC_KEY);
        CreatePublicKeyRequest request = new CreatePublicKeyRequest(PUBLIC_KEY, userData.deviceId());

        webTestClient.post()
                .uri("/keys")
                .header(AUTHORIZATION_HEADER, userData.authorizationHeader())
                .header(FS_USER_ID_HEADER, userData.user().getFsUserId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(PUBLIC_KEY_ALREADY_EXISTS.getCode());
    }

    @Test
    void should_ReturnNotFound_When_DeviceNotFound() {
        TestUserData userData = testDataProvider.createTestUserData();

        webTestClient.get()
                .uri("/keys/my")
                .header(AUTHORIZATION_HEADER, userData.authorizationHeader())
                .header(FS_USER_ID_HEADER, userData.user().getFsUserId().toString())
                .header(FS_DEVICE_ID_HEADER, userData.deviceId().toString())
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(DEVICE_NOT_FOUND.getCode());
    }

}
