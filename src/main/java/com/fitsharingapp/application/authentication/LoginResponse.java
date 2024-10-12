package com.fitsharingapp.application.authentication;

import java.util.UUID;

public record LoginResponse(
        UUID fsUserId,
        String token,
        long expiresIn

) {

}
