package com.fitsharingapp.application.user;

import com.fitsharingapp.application.authentication.dto.RegisterRequest;
import com.fitsharingapp.application.user.dto.UserResponse;
import com.fitsharingapp.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fsUserId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(source = "profilePicture", target = "profilePicture", qualifiedByName = "base64ToBytes")
    User toEntity(RegisterRequest userDTO);

    @Mapping(source = "profilePicture", target = "profilePicture", qualifiedByName = "bytesToBase64")
    UserResponse toResponse(User user);

    @Named("base64ToBytes")
    static byte[] base64ToBytes(String base64String) {
        if (base64String != null) {
            return Base64.getDecoder().decode(base64String.split(",")[1]);
        }
        return null;
    }

    @Named("bytesToBase64")
    static String bytesToBase64(byte[] imageBytes) {
        if (imageBytes != null) {
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        }
        return null;
    }



}
