package com.fitsharingapp.application.user.dto;

import com.fitsharingapp.domain.user.repository.UserGender;

import java.time.LocalDate;

public record UserResponse(

        String username,
        String email,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        UserGender gender,
        String description
) {

}
