package com.fitsharingapp.application.user;

import com.fitsharingapp.application.authentication.dto.RegisterRequest;
import com.fitsharingapp.application.user.dto.UserResponse;
import com.fitsharingapp.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fsUserId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "profilePicture", expression = "java(" +
            "com.fitsharingapp.application.common.Base64Utils.base64ToBytes(userDTO.profilePicture()))")
    User toEntity(RegisterRequest userDTO);

    @Mapping(target = "profilePicture", expression = "java(" +
            "com.fitsharingapp.application.common.Base64Utils.bytesToBase64(user.getProfilePicture()))")
    UserResponse toResponse(User user);

}
