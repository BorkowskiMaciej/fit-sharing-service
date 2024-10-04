package com.fitsharingapp.domain.user;

import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.user.dto.CreateUserDTO;
import com.fitsharingapp.domain.user.repository.User;
import com.fitsharingapp.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(CreateUserDTO createUserDTO) {
        validateUser(createUserDTO);
        return userRepository.save(userMapper.toEntity(createUserDTO));
    }

    private void validateUser(CreateUserDTO createUserDTO) {
        if (userRepository.existsByUsername(createUserDTO.username())) {
            throw new ServiceException(ErrorCode.NOT_UNIQUE_USERNAME);
        }
        if (userRepository.existsByEmail(createUserDTO.email())) {
            throw new ServiceException(ErrorCode.NOT_UNIQUE_EMAIL);
        }
    }

}
