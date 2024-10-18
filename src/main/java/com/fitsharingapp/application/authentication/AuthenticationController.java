package com.fitsharingapp.application.authentication;

import com.fitsharingapp.application.authentication.dto.LoginRequest;
import com.fitsharingapp.application.authentication.dto.LoginResponse;
import com.fitsharingapp.application.authentication.dto.RegisterRequest;
import com.fitsharingapp.application.authentication.dto.RegisterResponse;
import com.fitsharingapp.domain.user.UserService;
import com.fitsharingapp.domain.user.repository.User;
import com.fitsharingapp.security.AuthenticationService;
import com.fitsharingapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(CREATED)
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return new RegisterResponse(userService.createUser(registerRequest));
    }

    @PostMapping("/login")
    @ResponseStatus(OK)
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        User user = authenticationService.authenticate(loginRequest);
        return new LoginResponse(
                user.getFsUserId(),
                jwtService.generateToken(user),
                jwtService.getJwtExpiration());
    }

}