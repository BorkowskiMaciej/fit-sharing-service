package com.fitsharingapp.application.authentication.dto;

import java.util.UUID;

public record LoginResponse(
        UUID fsUserId,
        String token,
        long expiresIn

) {

}
