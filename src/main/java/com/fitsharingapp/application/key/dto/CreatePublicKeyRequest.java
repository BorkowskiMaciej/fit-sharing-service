package com.fitsharingapp.application.key.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePublicKeyRequest(

        @NotNull(message = "Public key must not be null")
        byte[] publicKey,

        @NotNull(message = "Device ID must not be null")
        UUID deviceId

) {

}
