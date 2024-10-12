package com.fitsharingapp.application.user.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record CreateUserRequest(

        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        int age,
        String description

) {

}
