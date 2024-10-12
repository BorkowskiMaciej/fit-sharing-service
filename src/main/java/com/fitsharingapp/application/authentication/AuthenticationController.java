package com.fitsharingapp.application.authentication;

import com.fitsharingapp.domain.user.UserService;
import com.fitsharingapp.domain.user.dto.CreateUserDTO;
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
    public User register(@RequestBody CreateUserDTO createUserDTO) {
        return userService.createUser(createUserDTO);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginUserDTO loginUserDto) {
        User user = authenticationService.authenticate(loginUserDto);
        return new LoginResponse(
                user.getFsUserId(),
                jwtService.generateToken(user),
                jwtService.getExpirationTime());
    }

}