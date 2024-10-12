package com.fitsharingapp.application.authentication;

import com.fitsharingapp.application.authentication.dto.LoginRequest;
import com.fitsharingapp.application.authentication.dto.LoginResponse;
import com.fitsharingapp.domain.user.UserService;
import com.fitsharingapp.application.user.dto.CreateUserRequest;
import com.fitsharingapp.domain.user.repository.User;
import com.fitsharingapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(CREATED)
    public User register(@RequestBody CreateUserRequest createUserRequest) {
        return userService.createUser(createUserRequest);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        User user = authenticationService.authenticate(loginRequest);
        return new LoginResponse(
                user.getFsUserId(),
                jwtService.generateToken(user),
                jwtService.getExpirationTime());
    }

}