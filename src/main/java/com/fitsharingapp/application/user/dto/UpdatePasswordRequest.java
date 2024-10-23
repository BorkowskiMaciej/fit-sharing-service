package com.fitsharingapp.application.user.dto;

public record UpdatePasswordRequest(
        String currentPassword,
        String newPassword
) {

}
