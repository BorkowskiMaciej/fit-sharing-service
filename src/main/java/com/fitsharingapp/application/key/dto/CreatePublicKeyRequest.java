package com.fitsharingapp.application.key.dto;

import java.util.UUID;

public record CreatePublicKeyRequest(

        byte[] publicKey,
        UUID deviceId

) {

}
