package com.fitsharingapp.application.authentication.dto;

public record ResetPasswordRequest(
        String email,
        String code,
        String newPassword

) {

}
