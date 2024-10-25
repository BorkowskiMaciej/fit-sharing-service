package com.fitsharingapp.application;

import com.fitsharingapp.EnableIntegrationContext;
import com.fitsharingapp.application.relationship.RelationshipService;
import com.fitsharingapp.application.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableIntegrationContext
public class NewsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserService userService;

    @Autowired
    private RelationshipService relationshipService;

//    @Test
//    void should_CreateNews_When_Requested() {
//
//        User publisher = userService.createUser(new RegisterRequest("username1", "username1@mail", "admin", "firstName", "lastName", 20, "description", "key".getBytes()));
//        User receiver = userService.createUser(new RegisterRequest("username2", "username2@mail", "admin", "firstName", "lastName", 20, "description", "key".getBytes()));
//        Relationship relationship = relationshipService.createRelationship(publisher.getFsUserId(), receiver.getFsUserId());
//        relationshipService.acceptRelationship(receiver.getFsUserId(), relationship.getId());
//        CreateNewsRequest
//                createNewsRequest = new CreateNewsRequest(receiver.getFsUserId(), UUID.randomUUID(), "data");
//
//        webTestClient.post()
//                .uri("/news")
//                .header(FS_USER_ID_HEADER, publisher.getFsUserId().toString())
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(createNewsRequest)
//                .exchange()
//                .expectStatus().isCreated();
//    }


}
