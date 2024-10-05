package com.fitsharingapp.domain.user.dto;

public record UpdateUserDTO(
        String firstName,
        String lastName,
        int age,
        String description

) {
}
