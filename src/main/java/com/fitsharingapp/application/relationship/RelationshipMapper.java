package com.fitsharingapp.application.relationship;

import com.fitsharingapp.application.relationship.dto.RelationshipResponse;
import com.fitsharingapp.domain.relationship.Relationship;
import com.fitsharingapp.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface RelationshipMapper {

    @Mapping(target = "relationshipId", source = "relationship.id")
    @Mapping(target = "friendFsUserId", source = "user.fsUserId")
    @Mapping(target = "friendUsername", source = "user.username")
    @Mapping(target = "friendFirstName", source = "user.firstName")
    @Mapping(target = "friendLastName", source = "user.lastName")
    @Mapping(target = "status", source = "relationship.status")
    @Mapping(target = "profilePicture", source = "user.profilePicture", qualifiedByName = "bytesToBase64")
    RelationshipResponse toRelationshipResponse(Relationship relationship, User user);

    @Named("bytesToBase64")
    static String bytesToBase64(byte[] imageBytes) {
        if (imageBytes != null) {
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        }
        return null;
    }

}
