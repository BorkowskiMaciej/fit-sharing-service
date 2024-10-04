package com.fitsharingapp.application;

import com.fitsharingapp.domain.user.dto.CreateUserDTO;
import com.fitsharingapp.domain.user.repository.User;
import com.fitsharingapp.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserDTO createUserDTO) {
        return userService.createUser(createUserDTO);
    }

}