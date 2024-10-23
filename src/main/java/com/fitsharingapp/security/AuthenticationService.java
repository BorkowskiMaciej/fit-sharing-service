package com.fitsharingapp.security;

import com.fitsharingapp.application.authentication.dto.LoginRequest;
import com.fitsharingapp.application.authentication.dto.ResetPasswordRequest;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.user.repository.User;
import com.fitsharingapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.fitsharingapp.common.ErrorCode.INVALID_RESET_PASSWORD_CODE;
import static com.fitsharingapp.common.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(LoginRequest input) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.username(), input.password()));
        return userRepository.findByUsername(input.username()).orElseThrow();
    }

    public void resetPasswordRequest(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
        log.info("Reset password code for user with email {}: {}",
                user.getEmail(),
                jwtService.generateResetPasswordToken(user));
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        try {
            if (!jwtService.isTokenValid(request.code(), user)) {
                throw new ServiceException(INVALID_RESET_PASSWORD_CODE);
            }
        }
        catch (Exception e) {
            throw new ServiceException(INVALID_RESET_PASSWORD_CODE);
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

}