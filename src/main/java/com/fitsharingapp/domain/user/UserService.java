package com.fitsharingapp.domain.user;

import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.user.dto.CreateUserDTO;
import com.fitsharingapp.domain.user.dto.UpdateUserDTO;
import com.fitsharingapp.domain.user.repository.User;
import com.fitsharingapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public List<User> searchByUsernameOrName(String searchTerm) {
        return userRepository.searchByUsernameOrName(searchTerm);
    }

    public User createUser(CreateUserDTO createUserDTO) {
        if (userRepository.existsByUsername(createUserDTO.username())) {
            throw new ServiceException(ErrorCode.NOT_UNIQUE_USERNAME);
        }
        if (userRepository.existsByEmail(createUserDTO.email())) {
            throw new ServiceException(ErrorCode.NOT_UNIQUE_EMAIL);
        }
        CreateUserDTO userDtoWithEncodedPassword = createUserDTO.toBuilder()
                .password(passwordEncoder.encode(createUserDTO.password()))
                .build();
        return userRepository.save(userMapper.toEntity(userDtoWithEncodedPassword));
    }

    public User updateUser(UUID fsUserId, UpdateUserDTO userUpdateDTO) {
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

}
