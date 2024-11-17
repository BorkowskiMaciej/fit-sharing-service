package com.fitsharingapp.application.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email is invalid")
        String email

) {

}
