package com.fitsharingapp.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-0001", "User not found"),
    NOT_UNIQUE_USERNAME(HttpStatus.BAD_REQUEST, "SERVICE-0002", "Username already exists"),
    NOT_UNIQUE_EMAIL(HttpStatus.BAD_REQUEST, "SERVICE-0003", "Email already exists");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
