package com.fitsharingapp.application.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDataRequest(

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email is invalid")
        String email,

        @NotBlank(message = "Reset code must not be blank")
        String code,

        @NotBlank(message = "New password must not be blank")
        @Size(min = 5, message = "New password must be at least 5 characters long")
        String newPassword

) {

}
