package com.fitsharingapp.domain.user;

import com.fitsharingapp.application.authentication.dto.RegisterRequest;
import com.fitsharingapp.application.user.dto.UpdateUserRequest;
import com.fitsharingapp.application.user.dto.UserResponse;
import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.key.PublicKeyService;
import com.fitsharingapp.domain.user.repository.User;
import com.fitsharingapp.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PublicKeyService publicKeyService;

    public User getUserById(UUID fsUserId, ErrorCode errorCode) {
        return userRepository.findById(fsUserId)
                .orElseThrow(() -> new ServiceException(errorCode));
    }

    public UserResponse getAuthenticatedUser(UUID fsUserId) {
        return userMapper.toResponse(getUserById(fsUserId, ErrorCode.USER_NOT_FOUND));
    }

    public String getUsernameById(UUID fsUserId, ErrorCode errorCode) {
        return userRepository.findById(fsUserId)
                .map(User::getUsername)
                .orElseThrow(() -> new ServiceException(errorCode));
    }

    public List<User> getUserBySearchTermWithoutAuthenticated(UUID fsUserId, String searchTerm) {
        return userRepository.getUserBySearchTermWithoutAuthenticated(searchTerm, fsUserId);
    }

    @Transactional
    public UUID createUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            throw new ServiceException(ErrorCode.NOT_UNIQUE_USERNAME);
        }
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new ServiceException(ErrorCode.NOT_UNIQUE_EMAIL);
        }
        RegisterRequest userDtoWithEncodedPassword = registerRequest.toBuilder()
                .password(passwordEncoder.encode(registerRequest.password()))
                .build();
        User user = userRepository.save(userMapper.toEntity(userDtoWithEncodedPassword));
        publicKeyService.savePublicKey(user.getFsUserId(), registerRequest.publicKey());
        return user.getFsUserId();
    }

    public User updateUser(UUID fsUserId, UpdateUserRequest userUpdateDTO) {
        User updatedUser = getUserById(fsUserId, ErrorCode.USER_NOT_FOUND)
                .toBuilder()
                .firstName(userUpdateDTO.firstName())
                .lastName(userUpdateDTO.lastName())
                .age(userUpdateDTO.age())
                .description(userUpdateDTO.description())
                .updatedAt(now())
                .build();
        return userRepository.save(updatedUser);
    }

    public void deleteUser(UUID fsUserId) {
        userRepository.deleteById(fsUserId);
    }

    public void validateUser(UUID fsUserId, ErrorCode errorCode) {
        userRepository.findById(fsUserId)
                .orElseThrow(() -> new ServiceException(errorCode));
    }

}
