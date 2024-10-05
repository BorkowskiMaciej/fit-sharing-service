package com.fitsharingapp.application;

import com.fitsharingapp.domain.relationship.repository.Relationship;
import com.fitsharingapp.domain.relationship.RelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/relationships")
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;

    @PostMapping("/send/{recipientUsername}")
    public Relationship sendRelationship(@RequestHeader UUID fsUserId, @PathVariable String recipientUsername) {
        return relationshipService.createRelationship(fsUserId, recipientUsername);
    }

    @PostMapping("/accept/{relationshipId}")
    public void acceptRelationship(@RequestHeader UUID fsUserId, @PathVariable UUID relationshipId) {
        relationshipService.acceptRelationship(fsUserId, relationshipId);
    }

    @PostMapping("/reject/{relationshipId}")
    public void rejectRelationship(@RequestHeader UUID fsUserId, @PathVariable UUID relationshipId) {
        relationshipService.rejectRelationship(fsUserId, relationshipId);
    }

    @DeleteMapping("/delete/{relationshipId}")
    public void deleteRelationship(@RequestHeader UUID fsUserId, @PathVariable UUID relationshipId) {
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