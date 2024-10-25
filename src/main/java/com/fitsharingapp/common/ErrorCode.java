package com.fitsharingapp.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // SERVICE-0xxx Authorization, headers, service errors
    MISSING_FS_USER_ID_HEADER(HttpStatus.BAD_REQUEST, "SERVICE-0013", "Missing fs-user-id in the request header"),
    MISSING_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "SERVICE-0020", "Missing authorization header"),
    NOT_AUTHENTICATED_USER_IN_HEADER(HttpStatus.NOT_FOUND, "SERVICE-0023", "Not authenticated user in the header"),
    INVALID_UUID_IN_HEADER(HttpStatus.BAD_REQUEST, "SERVICE-0014", "Invalid UUID in the %s header"),
    INVALID_TOKEN_DIFFERENT_USER(HttpStatus.BAD_REQUEST, "SERVICE-0025", "Invalid token, different user"),
    INVALID_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "SERVICE-0026", "Invalid token, expired"),
    INVALID_RESET_PASSWORD_CODE(HttpStatus.BAD_REQUEST, "SERVICE-0027", "Invalid reset password code."),
    INVALID_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "SERVICE-0029", "Invalid old password."),
    INVALID_DATA(HttpStatus.BAD_REQUEST, "SERVICE-0029", "Invalid data: %s."),

    // SERVICE-1xxx User, registration, login
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-0001", "User not found"),
    NOT_UNIQUE_USERNAME(HttpStatus.BAD_REQUEST, "SERVICE-0002", "Username already exists"),
    NOT_UNIQUE_EMAIL(HttpStatus.BAD_REQUEST, "SERVICE-0003", "Email already exists"),

    // SERVICE-2xxx Relationship
    RELATIONSHIP_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SERVICE-2001", "Relationship already exists"),
    PENDING_RELATIONSHIP(HttpStatus.BAD_REQUEST, "SERVICE-2002", "Relationship is pending"),
    RECIPIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-2003", "Recipient not found"),
    SELF_RELATIONSHIP(HttpStatus.BAD_REQUEST, "SERVICE-2004", "Self relationship"),
    RELATIONSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-2005", "Relationship not found"),
    USER_IS_NOT_SENDER(HttpStatus.BAD_REQUEST, "SERVICE-2006", "User is not the sender of the relationship"),
    USER_IS_NOT_RECIPIENT(HttpStatus.BAD_REQUEST, "SERVICE-2007", "User is not the recipient of the relationship"),
    RELATIONSHIP_HAS_NOT_PENDING_STATUS(HttpStatus.BAD_REQUEST, "SERVICE-2008", "Relationship is not in pending status"),
    CANNOT_DELETE_RELATIONSHIP(HttpStatus.BAD_REQUEST, "SERVICE-2009", "Cannot delete relationship, relationship is not accepted or user is not the sender"),

    // SERVICE-3xxx News
    RECEIVER_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-3000", "Receiver not found"),
    PUBLISHER_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-3001", "Publisher not found"),
    USER_IS_NOT_PUBLISHER(HttpStatus.BAD_REQUEST, "SERVICE-3002", "User is not the publisher of the news"),

    // SERVICE-4xxx Keys
    PUBLIC_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-4000", "Public key not found");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
