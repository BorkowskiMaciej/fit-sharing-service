package com.fitsharingapp.domain.user.dto;

public record CreateUserDTO(

        String username,
        String email,
        String firstName,
        String lastName,
        int age,
        String description

) {

}
