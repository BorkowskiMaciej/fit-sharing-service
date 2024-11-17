package com.fitsharingapp;

import com.fitsharingapp.domain.user.User;

import java.util.UUID;

public record TestUserData(
        User user,
        String fsUserIdHeader,
        String authorizationHeader,
        UUID deviceId,
        String deviceIdHeader) {

}
