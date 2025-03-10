package com.fitsharingapp.application.user;

import com.fitsharingapp.application.common.Base64Utils;
import com.fitsharingapp.application.common.validator.RequestValidator;
import com.fitsharingapp.application.user.dto.UpdatePasswordRequest;
import com.fitsharingapp.application.user.dto.UpdateUserRequest;
import com.fitsharingapp.application.user.dto.UserResponse;
import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.user.User;
import com.fitsharingapp.domain.user.UserGender;
import com.fitsharingapp.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.fitsharingapp.common.ErrorCode.INVALID_OLD_PASSWORD;
import static com.fitsharingapp.common.ErrorCode.USER_NOT_FOUND;
import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RequestValidator requestValidator;

    public UserResponse updateUser(UUID fsUserId, UpdateUserRequest userUpdateDTO) {
        requestValidator.validate(userUpdateDTO);
        User updatedUser = getUserById(fsUserId, USER_NOT_FOUND)
                .toBuilder()
                .firstName(userUpdateDTO.firstName())
                .lastName(userUpdateDTO.lastName())
                .gender(UserGender.validateAndGet(userUpdateDTO.gender()))
                .description(userUpdateDTO.description())
                .profilePicture(Base64Utils.base64ToBytes(userUpdateDTO.profilePicture()))
                .updatedAt(now())
                .build();
        return userMapper.toResponse(userRepository.save(updatedUser));
    }

    public void updatePassword(UUID fsUserId, UpdatePasswordRequest request) {
        requestValidator.validate(request);
        User user = getUserById(fsUserId, USER_NOT_FOUND);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new ServiceException(INVALID_OLD_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public UserResponse getUserResponseById(UUID fsUserId) {
        return userMapper.toResponse(getUserById(fsUserId, USER_NOT_FOUND));
    }

    public List<UserResponse> getUserBySearchTermWithoutAuthenticated(UUID fsUserId, String searchTerm) {
        return userRepository.getUserBySearchTermWithoutAuthenticated(searchTerm, fsUserId)
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public void deleteUser(UUID fsUserId) {
        userRepository.deleteById(fsUserId);
    }

    public User getUserById(UUID fsUserId, ErrorCode errorCode) {
        return userRepository.findById(fsUserId)
                .orElseThrow(() -> new ServiceException(errorCode));
    }

    public void validateUser(UUID fsUserId, ErrorCode errorCode) {
        userRepository.findById(fsUserId)
                .orElseThrow(() -> new ServiceException(errorCode));
    }

}
