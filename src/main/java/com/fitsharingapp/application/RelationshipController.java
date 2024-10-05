package com.fitsharingapp.application;

import com.fitsharingapp.domain.relationship.RelationshipService;
import com.fitsharingapp.domain.relationship.repository.Relationship;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(path = "/relationships")
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;

    @PostMapping("/send/{recipientFsUserId}")
    @ResponseStatus(CREATED)
    public Relationship sendRelationshipRequest(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId,
            @PathVariable UUID recipientFsUserId) {
        return relationshipService.createRelationship(fsUserId, recipientFsUserId);
    }

    @PostMapping("/accept/{relationshipId}")
    @ResponseStatus(ACCEPTED)
    public Relationship acceptRelationshipRequest(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId,
            @PathVariable UUID relationshipId) {
        return relationshipService.acceptRelationship(fsUserId, relationshipId);
    }

    @PostMapping("/reject/{relationshipId}")
    @ResponseStatus(NO_CONTENT)
    public void rejectRelationshipRequest(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId,
            @PathVariable UUID relationshipId) {
        relationshipService.rejectRelationship(fsUserId, relationshipId);
    }

    @DeleteMapping("/delete/{relationshipId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteRelationship(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId,
            @PathVariable UUID relationshipId) {
        relationshipService.deleteRelationship(fsUserId, relationshipId);
    }

    @GetMapping
    public List<Relationship> getRelationships() {
        return relationshipService.getAcceptedRelationships();
    }

    @GetMapping("/received")
    public List<Relationship> getReceivedRequests() {
        return relationshipService.getReceivedRelationshipRequests();
    }

    @GetMapping("/sent")
    public List<Relationship> getSentRequests() {
        return relationshipService.getSentRelationshipRequests();
    }

}