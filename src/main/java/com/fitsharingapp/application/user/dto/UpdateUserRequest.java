package com.fitsharingapp.application.user.dto;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        int age,
        String description

) {
}
