package com.fitsharingapp.application.authentication.dto;

import com.fitsharingapp.domain.user.UserGender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder(toBuilder = true)
public record RegisterRequest(
        @NotBlank(message = "Username must not be blank")
        @Size(max = 15, message = "Username must be between 1 and 15 characters long")
        String username,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email is invalid")
        String email,

        @NotBlank(message = "Password must not be blank")
        @Size(min = 5, message = "Password must be at least 5 characters long")
        String password,

        @NotBlank(message = "First name must not be blank")
        @Size(max = 15, message = "First name must be between 1 and 15 characters long")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        @Size(max = 15, message = "Last name must be between 1 and 15 characters long")
        String lastName,

        @NotNull(message = "Date of birth must not be null")
        LocalDate dateOfBirth,

        @NotNull(message = "Gender must not be null")
        UserGender gender,

        @Size(max = 200, message = "Description cannot exceed 200 characters")
        String description,

        @NotNull(message = "Public key must not be null")
        byte[] publicKey,

        String profilePicture,

        @NotNull(message = "Device ID must not be null")
        UUID deviceId

) {

}
