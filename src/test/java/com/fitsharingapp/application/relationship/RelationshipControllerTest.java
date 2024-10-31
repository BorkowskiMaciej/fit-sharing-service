package com.fitsharingapp.application.relationship;

import com.fitsharingapp.EnableIntegrationContext;
import com.fitsharingapp.TestDataProvider;
import com.fitsharingapp.TestUserData;
import com.fitsharingapp.application.key.PublicKeyService;
import com.fitsharingapp.application.relationship.dto.FriendsResponse;
import com.fitsharingapp.application.relationship.dto.RelationshipResponse;
import com.fitsharingapp.domain.relationship.Relationship;
import com.fitsharingapp.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static com.fitsharingapp.common.Constants.AUTHORIZATION_HEADER;
import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;
import static com.fitsharingapp.common.ErrorCode.*;
import static com.fitsharingapp.domain.relationship.RelationshipStatus.*;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableIntegrationContext
public class RelationshipControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataProvider testDataProvider;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private PublicKeyService publicKeyService;

    private TestUserData sender;
    private TestUserData recipient;

    @BeforeEach
    void setUp() {
        sender = testDataProvider.createTestUserData();
        recipient = testDataProvider.createTestUserData();
    }

    @Test
    void should_SendRelationshipRequest_When_Requested() {
        assertThat(relationshipService.getSentRelationshipRequests(sender.user().getFsUserId())).hasSize(0);

        webTestClient.post()
                .uri("/relationships/send/{recipientFsUserId}", recipient.user().getFsUserId())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isCreated();

        List<RelationshipResponse> relationships = relationshipService.getSentRelationshipRequests(sender.user().getFsUserId());
        assertThat(relationships).hasSize(1);
        assertThat(relationships.get(0).friendFsUserId()).isEqualTo(recipient.user().getFsUserId());
        assertThat(relationships.get(0).status()).isEqualTo(PENDING);
    }

    @Test
    void should_AcceptRelationshipRequest_When_Requested() {
        Relationship relationship = testDataProvider.createRelationship(
                sender.user().getFsUserId(), recipient.user().getFsUserId(), PENDING);

        assertThat(relationshipService.getAcceptedRelationships(sender.user().getFsUserId())).hasSize(0);

        webTestClient.post()
                .uri("/relationships/accept/{relationshipId}", relationship.getId())
                .header(AUTHORIZATION_HEADER, recipient.authorizationHeader())
                .header(FS_USER_ID_HEADER, recipient.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isOk();

        List<RelationshipResponse> relationships = relationshipService.getAcceptedRelationships(sender.user().getFsUserId());
        assertThat(relationships).hasSize(1);
        assertThat(relationships.get(0).friendFsUserId()).isEqualTo(recipient.user().getFsUserId());
        assertThat(relationships.get(0).status()).isEqualTo(ACCEPTED);
    }

    @Test
    void should_RejectRelationshipRequest_When_Requested() {
        Relationship relationship = testDataProvider.createRelationship(
                sender.user().getFsUserId(), recipient.user().getFsUserId(), PENDING);

        assertThat(relationshipService.getSentRelationshipRequests(sender.user().getFsUserId())).hasSize(1);

        webTestClient.post()
                .uri("/relationships/reject/{relationshipId}", relationship.getId())
                .header(AUTHORIZATION_HEADER, recipient.authorizationHeader())
                .header(FS_USER_ID_HEADER, recipient.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isOk();

        assertThat(relationshipService.getSentRelationshipRequests(sender.user().getFsUserId())).hasSize(0);

    }

    @Test
    void should_DeleteRelationshipRequest_When_Requested() {
        Relationship relationship = testDataProvider.createRelationship(
                sender.user().getFsUserId(), recipient.user().getFsUserId(), PENDING);

        assertThat(relationshipService.getSentRelationshipRequests(sender.user().getFsUserId())).hasSize(1);

        webTestClient.delete()
                .uri("/relationships/delete/{relationshipId}", relationship.getId())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isNoContent();

        assertThat(relationshipService.getSentRelationshipRequests(sender.user().getFsUserId())).hasSize(0);
    }

    @Test
    void should_DeleteRelationship_When_Requested() {
        Relationship relationship = testDataProvider.createRelationship(
                sender.user().getFsUserId(), recipient.user().getFsUserId(), ACCEPTED);

        assertThat(relationshipService.getAcceptedRelationships(sender.user().getFsUserId())).hasSize(1);

        webTestClient.delete()
                .uri("/relationships/delete/{relationshipId}", relationship.getId())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isNoContent();

        assertThat(relationshipService.getAcceptedRelationships(sender.user().getFsUserId())).hasSize(0);
    }

    @Test
    void should_ReturnActiveRelationship_When_Requested() {
        testDataProvider.createRelationship(sender.user().getFsUserId(), recipient.user().getFsUserId(), ACCEPTED);

        webTestClient.get()
                .uri("/relationships/" + recipient.user().getFsUserId())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(RelationshipResponse.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).isNotNull());
    }

    @Test
    void should_ReturnAcceptedRelationships_When_Requested() {
        User sender2 = testDataProvider.createAndSaveRandomUser();
        testDataProvider.createRelationship(sender.user().getFsUserId(), recipient.user().getFsUserId(), ACCEPTED);
        testDataProvider.createRelationship(sender2.getFsUserId(), sender.user().getFsUserId(), ACCEPTED);

        List<RelationshipResponse> response = webTestClient.get()
                .uri("/relationships/accepted")
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RelationshipResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).hasSize(2);
    }

    @Test
    void should_ReturnReceivedRelationshipRequests_When_Requested() {
        User sender2 = testDataProvider.createAndSaveRandomUser();
        testDataProvider.createRelationship(sender.user().getFsUserId(), recipient.user().getFsUserId(), PENDING);
        testDataProvider.createRelationship(sender2.getFsUserId(), sender.user().getFsUserId(), PENDING);

        List<RelationshipResponse> response = webTestClient.get()
                .uri("/relationships/received")
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RelationshipResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).hasSize(1);
    }

    @Test
    void should_ReturnSentRelationships_When_Requested() {
        User sender2 = testDataProvider.createAndSaveRandomUser();
        testDataProvider.createRelationship(sender.user().getFsUserId(), recipient.user().getFsUserId(), PENDING);
        testDataProvider.createRelationship(sender2.getFsUserId(), sender.user().getFsUserId(), PENDING);

        List<RelationshipResponse> response = webTestClient.get()
                .uri("/relationships/sent")
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(RelationshipResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).hasSize(1);
    }

    @Test
    void should_ReturnFriends_When_Requested() {
        byte[] public_key = "PUBLIC_KEY".getBytes();
        TestUserData sender2 = testDataProvider.createTestUserData();
        testDataProvider.createRelationship(sender.user().getFsUserId(), recipient.user().getFsUserId(), ACCEPTED);
        testDataProvider.createRelationship(sender2.user().getFsUserId(), sender.user().getFsUserId(), ACCEPTED);
        publicKeyService.savePublicKey(recipient.user().getFsUserId(), recipient.deviceId(), public_key);
        publicKeyService.savePublicKey(sender2.user().getFsUserId(), sender2.deviceId(), public_key);

        List<FriendsResponse> response = webTestClient.get()
                .uri("/relationships/friends")
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(FriendsResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).hasSize(2);
    }

    @Test
    void should_ReturnBadRequest_When_DeleteRejectedRelationship() {
        Relationship relationship = testDataProvider.createRelationship(
                sender.user().getFsUserId(), recipient.user().getFsUserId(), REJECTED);

        webTestClient.delete()
                .uri("/relationships/delete/{relationshipId}", relationship.getId())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code")
                .isEqualTo(CANNOT_DELETE_RELATIONSHIP.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_RecipientDeleteRelationshipRequest() {
        Relationship relationship = testDataProvider.createRelationship(
                sender.user().getFsUserId(), recipient.user().getFsUserId(), PENDING);

        webTestClient.delete()
                .uri("/relationships/delete/{relationshipId}", relationship.getId())
                .header(AUTHORIZATION_HEADER, recipient.authorizationHeader())
                .header(FS_USER_ID_HEADER, recipient.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(USER_IS_NOT_SENDER.getCode());
    }

    @Test
    void should_ReturnNotFound_When_DeleteNotExistingRelationship() {
        webTestClient.delete()
                .uri("/relationships/delete/{relationshipId}", randomUUID())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(RELATIONSHIP_NOT_FOUND.getCode());
    }

    @Test
    void should_ReturnNotFound_When_GetNotExistingRelationship() {
        webTestClient.get()
                .uri("/relationships/" + recipient.user().getFsUserId())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(RELATIONSHIP_NOT_FOUND.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_AcceptNotPendingRelationship() {
        Relationship relationship = testDataProvider.createRelationship(
                sender.user().getFsUserId(), recipient.user().getFsUserId(), ACCEPTED);

        webTestClient.post()
                .uri("/relationships/accept/{relationshipId}", relationship.getId())
                .header(AUTHORIZATION_HEADER, recipient.authorizationHeader())
                .header(FS_USER_ID_HEADER, recipient.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(RELATIONSHIP_HAS_NOT_PENDING_STATUS.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_SenderAcceptsRelationship() {
        Relationship relationship = testDataProvider.createRelationship(
                sender.user().getFsUserId(), recipient.user().getFsUserId(), ACCEPTED);

        webTestClient.post()
                .uri("/relationships/accept/{relationshipId}", relationship.getId())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(USER_IS_NOT_RECIPIENT.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_AcceptNotExistingRelationship() {
        webTestClient.post()
                .uri("/relationships/accept/{relationshipId}", randomUUID())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(RELATIONSHIP_NOT_FOUND.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_RelationshipAlreadyExists() {
        testDataProvider.createRelationship(sender.user().getFsUserId(), recipient.user().getFsUserId(), ACCEPTED);

        webTestClient.post()
                .uri("/relationships/send/{recipientFsUserId}", recipient.user().getFsUserId())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(RELATIONSHIP_ALREADY_EXISTS.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_SendSelfRelationshipRequest() {
        webTestClient.post()
                .uri("/relationships/send/{recipientFsUserId}", sender.user().getFsUserId())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(SELF_RELATIONSHIP.getCode());
    }

    @Test
    void should_ReturnBadRequest_When_PendingRelationshipRequestAlreadyExists() {
        testDataProvider.createRelationship(sender.user().getFsUserId(), recipient.user().getFsUserId(), PENDING);

        webTestClient.post()
                .uri("/relationships/send/{recipientFsUserId}", recipient.user().getFsUserId())
                .header(AUTHORIZATION_HEADER, sender.authorizationHeader())
                .header(FS_USER_ID_HEADER, sender.fsUserIdHeader())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(PENDING_RELATIONSHIP.getCode());
    }

}
