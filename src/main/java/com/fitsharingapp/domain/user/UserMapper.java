package com.fitsharingapp.domain.user;

import com.fitsharingapp.domain.user.dto.CreateUserDTO;
import com.fitsharingapp.domain.user.repository.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(CreateUserDTO userDTO);

}
