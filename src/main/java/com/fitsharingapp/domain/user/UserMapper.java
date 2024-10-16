package com.fitsharingapp.domain.user;

import com.fitsharingapp.application.user.dto.CreateUserRequest;
import com.fitsharingapp.application.user.dto.UserResponse;
import com.fitsharingapp.domain.user.repository.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fsUserId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(CreateUserRequest userDTO);

    UserResponse toResponse(User user);

}
