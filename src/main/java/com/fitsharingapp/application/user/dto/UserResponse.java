package com.fitsharingapp.application.user.dto;

import com.fitsharingapp.domain.user.UserGender;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(

        UUID fsUserId,
        String username,
        String email,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        UserGender gender,
        String description,
        String profilePicture
) {

}
