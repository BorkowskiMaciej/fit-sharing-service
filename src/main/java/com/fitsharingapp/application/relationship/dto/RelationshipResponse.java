package com.fitsharingapp.application.relationship.dto;

import com.fitsharingapp.domain.relationship.RelationshipStatus;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record RelationshipResponse(

        UUID relationshipId,
        UUID friendFsUserId,
        String friendUsername,
        String friendFirstName,
        String friendLastName,
        RelationshipStatus status,
        String profilePicture

) {

}
