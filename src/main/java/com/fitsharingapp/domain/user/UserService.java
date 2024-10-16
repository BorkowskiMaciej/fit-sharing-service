package com.fitsharingapp.domain.user;

import com.fitsharingapp.application.user.dto.UserResponse;
import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.application.user.dto.CreateUserRequest;
import com.fitsharingapp.application.user.dto.UpdateUserRequest;
import com.fitsharingapp.domain.user.repository.User;
import com.fitsharingapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(UUID fsUserId) {
        return userRepository.findById(fsUserId);
    }

    public String getUserNameById(UUID fsUserId) {
        return userRepository.findById(fsUserId)
                .map(User::getUsername)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
    }

    public List<User> searchByUsernameOrName(String searchTerm) {
        User authenticated = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.searchByUsernameOrName(searchTerm, authenticated.getFsUserId());
    }

    public User createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByUsername(createUserRequest.username())) {
            throw new ServiceException(ErrorCode.NOT_UNIQUE_USERNAME);
        }
        if (userRepository.existsByEmail(createUserRequest.email())) {
            throw new ServiceException(ErrorCode.NOT_UNIQUE_EMAIL);
        }
        CreateUserRequest userDtoWithEncodedPassword = createUserRequest.toBuilder()
                .password(passwordEncoder.encode(createUserRequest.password()))
                .build();
        return userRepository.save(userMapper.toEntity(userDtoWithEncodedPassword));
    }

    public User updateUser(UUID fsUserId, UpdateUserRequest userUpdateDTO) {
        User user = userRepository.findById(fsUserId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));
        user.setFirstName(userUpdateDTO.firstName());
        user.setLastName(userUpdateDTO.lastName());
        user.setAge(userUpdateDTO.age());
        user.setDescription(userUpdateDTO.description());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public void deleteUser(UUID fsUserId) {
        userRepository.deleteById(fsUserId);
    }

    public void validateUser(UUID fsUserId, ErrorCode errorCode) {
        userRepository.findById(fsUserId)
                .orElseThrow(() -> new ServiceException(errorCode));
    }

    public UserResponse getAuthenticatedUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.toResponse(user);
    }

}
