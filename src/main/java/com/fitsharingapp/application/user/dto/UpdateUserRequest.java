package com.fitsharingapp.application.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "First name must not be blank")
        @Size(max = 15, message = "First name must be between 1 and 15 characters long")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        @Size(max = 15, message = "Last name must be between 1 and 15 characters long")
        String lastName,

        @Size(max = 200, message = "Description cannot exceed 200 characters")
        String description,

        @NotNull(message = "Gender must not be null")
        String gender,

        String profilePicture

) {
}
