package com.fitsharingapp.domain.user.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record CreateUserDTO(

        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        int age,
        String description

) {

}
