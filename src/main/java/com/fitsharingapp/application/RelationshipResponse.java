package com.fitsharingapp.application;

import com.fitsharingapp.domain.relationship.repository.RelationshipStatus;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record RelationshipResponse(

        UUID relationshipId,
        UUID friendFsUserId,
        String friendUsername,
        String friendFirstName,
        String friendLastName,
        RelationshipStatus status

) {

}
