package com.fitsharingapp.application.authentication;

import com.fitsharingapp.application.authentication.dto.LoginRequest;
import com.fitsharingapp.domain.user.repository.User;
import com.fitsharingapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public User authenticate(LoginRequest input) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.username(), input.password()));
        return userRepository.findByUsername(input.username()).orElseThrow();
    }

}