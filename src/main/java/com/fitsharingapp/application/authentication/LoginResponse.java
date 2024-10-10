package com.fitsharingapp.application.authentication;

public record LoginResponse(
        String token,
        long expiresIn
) {

}
