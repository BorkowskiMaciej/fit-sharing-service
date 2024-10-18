package com.fitsharingapp.domain.relationship;

import com.fitsharingapp.application.relationship.dto.RelationshipResponse;
import com.fitsharingapp.domain.relationship.repository.Relationship;
import com.fitsharingapp.domain.user.repository.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RelationshipMapper {

    @Mapping(target = "relationshipId", source = "relationship.id")
    @Mapping(target = "friendFsUserId", source = "user.fsUserId")
    @Mapping(target = "friendUsername", source = "user.username")
    @Mapping(target = "friendFirstName", source = "user.firstName")
    @Mapping(target = "friendLastName", source = "user.lastName")
    @Mapping(target = "status", source = "relationship.status")
    RelationshipResponse toRelationshipResponse(Relationship relationship, User user);

}
