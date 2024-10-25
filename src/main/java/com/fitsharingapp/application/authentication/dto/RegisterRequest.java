package com.fitsharingapp.application.authentication.dto;

import com.fitsharingapp.domain.user.repository.UserGender;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder(toBuilder = true)
public record RegisterRequest(
        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        UserGender gender,
        String description,
        byte[] publicKey,
        String profilePicture,
        UUID deviceId

) {

}
