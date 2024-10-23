package com.fitsharingapp.application.user.dto;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String description,
        String gender

) {
}
