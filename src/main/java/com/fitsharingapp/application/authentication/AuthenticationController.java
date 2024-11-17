package com.fitsharingapp.application.authentication;

import com.fitsharingapp.application.authentication.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(CREATED)
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return new RegisterResponse(authenticationService.createUser(registerRequest));
    }

    @PostMapping("/login")
    @ResponseStatus(OK)
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.authenticate(loginRequest);
    }

    @PostMapping("/reset-password-request")
    @ResponseStatus(OK)
    public void resetPasswordRequest(@RequestBody ResetPasswordRequest request) {
        authenticationService.resetPasswordRequest(request);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(OK)
    public void resetPassword(@RequestBody ResetPasswordDataRequest request) {
        authenticationService.resetPassword(request);
    }

}