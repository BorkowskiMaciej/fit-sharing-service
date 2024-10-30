package com.fitsharingapp;

import com.fitsharingapp.application.authentication.dto.RegisterRequest;
import com.fitsharingapp.application.user.UserMapper;
import com.fitsharingapp.domain.user.User;
import com.fitsharingapp.domain.user.UserGender;
import com.fitsharingapp.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.UUID.randomUUID;

@Component
@RequiredArgsConstructor
public class TestDataProvider {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public RegisterRequest createRandomRegisterRequest() {
        String username = "user" + ThreadLocalRandom.current().nextInt(1000, 9999);
        String email = username + "@example.com";
        String password = "password";
        String firstName = "firstName";
        String lastName = "lastName";
        LocalDate dateOfBirth = LocalDate.now().minusYears(20);
        UserGender gender = UserGender.OTHER;
        String description = "Description";
        byte[] publicKey = "PUBLIC_KEY".getBytes();
        String profilePicture = null;
        UUID deviceId = randomUUID();

        return new RegisterRequest(username, email, passwordEncoder.encode(password), firstName, lastName, dateOfBirth,
                gender, description, publicKey, profilePicture, deviceId);
    }

    public User createAndSaveRandomUser() {
        return userRepository.save(userMapper.toEntity(createRandomRegisterRequest()));
    }



}
