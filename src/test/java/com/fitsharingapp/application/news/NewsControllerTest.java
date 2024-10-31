package com.fitsharingapp.application.news;

import com.fitsharingapp.EnableIntegrationContext;
import com.fitsharingapp.TestDataProvider;
import com.fitsharingapp.TestUserData;
import com.fitsharingapp.application.key.PublicKeyService;
import com.fitsharingapp.application.news.dto.CreateNewsRequest;
import com.fitsharingapp.application.news.dto.CreateReferenceNewsRequest;
import com.fitsharingapp.application.news.dto.NewsResponse;
import com.fitsharingapp.domain.news.ReferenceNews;
import com.fitsharingapp.domain.relationship.RelationshipStatus;
import com.fitsharingapp.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.fitsharingapp.common.Constants.*;
import static com.fitsharingapp.common.ErrorCode.RELATIONSHIP_NOT_FOUND;
import static com.fitsharingapp.common.ErrorCode.USER_IS_NOT_PUBLISHER;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableIntegrationContext
public class NewsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TestDataProvider testDataProvider;

    @Autowired
    private NewsService newsService;

    @Autowired
    private PublicKeyService publicKeyService;

    private TestUserData publisher;
    private TestUserData receiver;

    @BeforeEach
    void setUp() {
        publisher = testDataProvider.createTestUserData();
        receiver = testDataProvider.createTestUserData();
        testDataProvider.createRelationship(
                publisher.user().getFsUserId(), receiver.user().getFsUserId(), RelationshipStatus.ACCEPTED);
        publicKeyService.savePublicKey(publisher.user().getFsUserId(), publisher.deviceId(), "PUBLIC_KEY".getBytes());
        publicKeyService.savePublicKey(receiver.user().getFsUserId(), receiver.deviceId(), "PUBLIC_KEY".getBytes());
    }

    @Test
    void should_CreateNews_When_Requested() {
        CreateNewsRequest request = new CreateNewsRequest(
                randomUUID(), receiver.user().getFsUserId(), receiver.deviceId(), "Data");

        assertThat(newsService.getAllReceivedNews(receiver.user().getFsUserId(), receiver.deviceId())).hasSize(0);

        webTestClient.post()
                .uri("/news")
                .header(AUTHORIZATION_HEADER, publisher.authorizationHeader())
                .header(FS_USER_ID_HEADER, publisher.fsUserIdHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        assertThat(newsService.getAllReceivedNews(receiver.user().getFsUserId(), receiver.deviceId())).hasSize(1);
    }

    @Test
    void should_CreateReferenceNews_When_Requested() {
        CreateReferenceNewsRequest request = new CreateReferenceNewsRequest("Data");

        assertThat(newsService.getAllPublishedNews(publisher.user().getFsUserId(), publisher.deviceId())).hasSize(0);

        webTestClient.post()
                .uri("/news/reference")
                .header(AUTHORIZATION_HEADER, publisher.authorizationHeader())
                .header(FS_USER_ID_HEADER, publisher.fsUserIdHeader())
                .header(FS_DEVICE_ID_HEADER, publisher.deviceIdHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated();

        assertThat(newsService.getAllPublishedNews(publisher.user().getFsUserId(), publisher.deviceId())).hasSize(1);
    }

    @Test
    void should_ReturnAllPublishedNews_When_Requested() {
        CreateReferenceNewsRequest referenceNews = new CreateReferenceNewsRequest("Data");
        newsService.createReferenceNews(publisher.user().getFsUserId(), publisher.deviceId(), referenceNews);

        webTestClient.get()
                .uri("/news/reference")
                .header(AUTHORIZATION_HEADER, publisher.authorizationHeader())
                .header(FS_USER_ID_HEADER, publisher.fsUserIdHeader())
                .header(FS_DEVICE_ID_HEADER, publisher.deviceId().toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(NewsResponse.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).hasSize(1));
    }

    @Test
    void should_ReturnAllReceivedNews_When_Requested() {
        CreateNewsRequest news = new CreateNewsRequest(
                randomUUID(), receiver.user().getFsUserId(), receiver.deviceId(), "Data");
        newsService.createNews(publisher.user().getFsUserId(), news);

        User publisher2 = testDataProvider.createAndSaveRandomUser();
        testDataProvider.createRelationship(
                publisher2.getFsUserId(), receiver.user().getFsUserId(), RelationshipStatus.ACCEPTED);
        newsService.createNews(publisher2.getFsUserId(), news);

        webTestClient.get()
                .uri("/news/received")
                .header(AUTHORIZATION_HEADER, receiver.authorizationHeader())
                .header(FS_USER_ID_HEADER, receiver.fsUserIdHeader())
                .header(FS_DEVICE_ID_HEADER, receiver.deviceId().toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(NewsResponse.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).hasSize(2));
    }

    @Test
    void should_ReturnAllNewsFromFriend_When_Requested() {
        CreateNewsRequest news = new CreateNewsRequest(
                randomUUID(), receiver.user().getFsUserId(), receiver.deviceId(), "Data");
        newsService.createNews(publisher.user().getFsUserId(), news);

        User publisher2 = testDataProvider.createAndSaveRandomUser();
        testDataProvider.createRelationship(
                publisher2.getFsUserId(), receiver.user().getFsUserId(), RelationshipStatus.ACCEPTED);
        newsService.createNews(publisher2.getFsUserId(), news);

        webTestClient.get()
                .uri("/news/received/" + publisher.fsUserIdHeader())
                .header(AUTHORIZATION_HEADER, receiver.authorizationHeader())
                .header(FS_USER_ID_HEADER, receiver.fsUserIdHeader())
                .header(FS_DEVICE_ID_HEADER, receiver.deviceId().toString())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(NewsResponse.class)
                .consumeWith(response -> assertThat(response.getResponseBody()).hasSize(1));
    }

    @Test
    void should_DeleteNews_When_Requested() {
        CreateReferenceNewsRequest referenceNewsRequest = new CreateReferenceNewsRequest("Data");
        ReferenceNews referenceNews = newsService.createReferenceNews(publisher.user().getFsUserId(), publisher.deviceId(), referenceNewsRequest);
        CreateNewsRequest newsRequest = new CreateNewsRequest(
                referenceNews.getId(), receiver.user().getFsUserId(), receiver.deviceId(), "Data");
        newsService.createNews(publisher.user().getFsUserId(), newsRequest);

        assertThat(newsService.getAllPublishedNews(publisher.user().getFsUserId(), publisher.deviceId())).hasSize(1);
        assertThat(newsService.getAllReceivedNews(receiver.user().getFsUserId(), receiver.deviceId())).hasSize(1);

        webTestClient.delete()
                .uri("/news/" + referenceNews.getId())
                .header(AUTHORIZATION_HEADER, publisher.authorizationHeader())
                .header(FS_USER_ID_HEADER, publisher.fsUserIdHeader())
                .header(FS_DEVICE_ID_HEADER, publisher.deviceId().toString())
                .exchange()
                .expectStatus()
                .isNoContent();

        assertThat(newsService.getAllPublishedNews(publisher.user().getFsUserId(), publisher.deviceId())).hasSize(0);
        assertThat(newsService.getAllReceivedNews(receiver.user().getFsUserId(), receiver.deviceId())).hasSize(0);
    }

    @Test
    void should_ReturnBadRequest_When_DeleteNotOwnNews() {
        CreateReferenceNewsRequest referenceNewsRequest = new CreateReferenceNewsRequest("Data");
        ReferenceNews referenceNews = newsService.createReferenceNews(publisher.user().getFsUserId(), publisher.deviceId(), referenceNewsRequest);

        webTestClient.delete()
                .uri("/news/" + referenceNews.getId())
                .header(AUTHORIZATION_HEADER, receiver.authorizationHeader())
                .header(FS_USER_ID_HEADER, receiver.fsUserIdHeader())
                .header(FS_DEVICE_ID_HEADER, receiver.deviceId().toString())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(USER_IS_NOT_PUBLISHER.getCode());

    }

    @Test
    void should_ReturnBadRequest_When_CreateNewsAndRelationshipDoesNotExist() {
        TestUserData publisher2 = testDataProvider.createTestUserData();
        CreateNewsRequest news = new CreateNewsRequest(
                randomUUID(), receiver.user().getFsUserId(), receiver.deviceId(), "Data");

        webTestClient.post()
                .uri("/news")
                .header(AUTHORIZATION_HEADER, publisher2.authorizationHeader())
                .header(FS_USER_ID_HEADER, publisher2.fsUserIdHeader())
                .header(FS_DEVICE_ID_HEADER, publisher2.deviceId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(news)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo(RELATIONSHIP_NOT_FOUND.getCode());

    }

}
