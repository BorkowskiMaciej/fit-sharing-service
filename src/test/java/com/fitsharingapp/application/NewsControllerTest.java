package com.fitsharingapp.application;

import com.fitsharingapp.EnableIntegrationContext;
import com.fitsharingapp.application.news.CreateNewsRequest;
import com.fitsharingapp.application.user.dto.CreateUserRequest;
import com.fitsharingapp.domain.relationship.RelationshipService;
import com.fitsharingapp.domain.relationship.repository.Relationship;
import com.fitsharingapp.domain.user.UserService;
import com.fitsharingapp.domain.user.repository.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableIntegrationContext
public class NewsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserService userService;

    @Autowired
    private RelationshipService relationshipService;

    @Test
    void should_CreateNews_When_Requested() {

        User publisher = userService.createUser(new CreateUserRequest("username1", "username1@mail", "admin", "firstName", "lastName", 20, "desctiption"));
        User receiver = userService.createUser(new CreateUserRequest("username2", "username2@mail", "admin", "firstName", "lastName", 20, "desctiption"));
        Relationship relationship = relationshipService.createRelationship(publisher.getFsUserId(), receiver.getFsUserId());
        relationshipService.acceptRelationship(receiver.getFsUserId(), relationship.getId());
        CreateNewsRequest
                createNewsRequest = new CreateNewsRequest(receiver.getFsUserId(), "data");

        webTestClient.post()
                .uri("/news")
                .header(FS_USER_ID_HEADER, publisher.getFsUserId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createNewsRequest)
                .exchange()
                .expectStatus().isCreated();
    }


}
