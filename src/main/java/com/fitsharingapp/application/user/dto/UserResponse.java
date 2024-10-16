package com.fitsharingapp.application.user.dto;

public record UserResponse(

        String username,
        String email,
        String firstName,
        String lastName,
        int age,
        String description
) {

}
