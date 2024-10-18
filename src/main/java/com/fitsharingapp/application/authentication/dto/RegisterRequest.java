package com.fitsharingapp.application.authentication.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record RegisterRequest(

        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        int age,
        String description,
        byte[] publicKey

) {

}
