package com.fitsharingapp.security;

import com.fitsharingapp.application.authentication.dto.*;
import com.fitsharingapp.application.common.validator.RequestValidator;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.key.PublicKeyService;
import com.fitsharingapp.domain.user.UserMapper;
import com.fitsharingapp.domain.user.repository.User;
import com.fitsharingapp.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.fitsharingapp.common.ErrorCode.*;
import static com.fitsharingapp.common.ErrorCode.NOT_UNIQUE_EMAIL;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PublicKeyService publicKeyService;
    private final RequestValidator requestValidator;

    @Transactional
    public UUID createUser(RegisterRequest registerRequest) {
        requestValidator.validate(registerRequest);
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new ServiceException(NOT_UNIQUE_USERNAME);
        }
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new ServiceException(NOT_UNIQUE_EMAIL);
        }
        RegisterRequest userDtoWithEncodedPassword = registerRequest.toBuilder()
                .password(passwordEncoder.encode(registerRequest.password()))
                .build();
        User user = userRepository.save(userMapper.toEntity(userDtoWithEncodedPassword));
        publicKeyService.savePublicKey(user.getFsUserId(), registerRequest.deviceId(), registerRequest.publicKey());
        return user.getFsUserId();
    }

    public LoginResponse authenticate(LoginRequest input) {
        requestValidator.validate(input);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.username(), input.password()));
        User user = userRepository.findByUsername(input.username())
                .orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
        return new LoginResponse(
                user.getFsUserId(),
                jwtService.generateToken(user),
                jwtService.getJwtExpiration());
    }

    public void resetPasswordRequest(ResetPasswordRequest request) {
        requestValidator.validate(request);
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
        log.info("Reset password code for user with email {}: {}",
                user.getEmail(),
                jwtService.generateResetPasswordToken(user));
    }

    public void resetPassword(ResetPasswordDataRequest request) {
        requestValidator.validate(request);
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(USER_NOT_FOUND));
        try {
            if (!jwtService.isTokenValid(request.code(), user)) {
                throw new Exception();
            }
        }
        catch (Exception e) {
            throw new ServiceException(INVALID_RESET_PASSWORD_CODE);
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

}