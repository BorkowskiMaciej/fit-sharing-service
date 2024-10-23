package com.fitsharingapp.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-0001", "User not found"),
    NOT_UNIQUE_USERNAME(HttpStatus.BAD_REQUEST, "SERVICE-0002", "Username already exists"),
    NOT_UNIQUE_EMAIL(HttpStatus.BAD_REQUEST, "SERVICE-0003", "Email already exists"),
    RELATIONSHIP_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SERVICE-0004", "Relationship already exists"),
    PENDING_RELATIONSHIP(HttpStatus.BAD_REQUEST, "SERVICE-0005", "Relationship is pending"),
    RECIPIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-0006", "Recipient not found"),
    SELF_RELATIONSHIP(HttpStatus.BAD_REQUEST, "SERVICE-0007", "Self relationship"),
    RELATIONSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-0008", "Relationship not found"),
    USER_IS_NOT_SENDER(HttpStatus.BAD_REQUEST, "SERVICE-0009", "User is not the sender of the relationship"),
    USER_IS_NOT_RECIPIENT(HttpStatus.BAD_REQUEST, "SERVICE-0010", "User is not the recipient of the relationship"),
    RELATIONSHIP_HAS_NOT_PENDING_STATUS(HttpStatus.BAD_REQUEST, "SERVICE-0011", "Relationship is not in pending status"),
    CANNOT_DELETE_RELATIONSHIP(HttpStatus.BAD_REQUEST, "SERVICE-0012", "Cannot delete relationship, relationship is not accepted or user is not the sender"),
    MISSING_FS_USER_ID_HEADER(HttpStatus.BAD_REQUEST, "SERVICE-0013", "Missing fs-user-id in the request header"),
    INVALID_UUID_IN_HEADER(HttpStatus.BAD_REQUEST, "SERVICE-0014", "Invalid UUID in the %s header"),
    RECEIVER_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-0015", "Receiver not found"),
    INVALID_ACTIVITY_TYPE(HttpStatus.BAD_REQUEST, "SERVICE-0016", "Invalid activity type"),
    NOT_ACCEPTED_RELATIONSHIP(HttpStatus.BAD_REQUEST, "SERVICE-0017", "Relationship is not accepted"),
    NEWS_IS_NOT_PUBLISHED_BY_USER(HttpStatus.BAD_REQUEST, "SERVICE-0018", "News is not published by the user"),
    NEWS_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-0019", "News not found"),
    MISSING_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "SERVICE-0020", "Missing authorization header"),
    PUBLIC_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-0021", "Public key not found"),
    PUBLISHER_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-0022", "Publisher not found"),
    NOT_AUTHENTICATED_USER_IN_HEADER(HttpStatus.NOT_FOUND, "SERVICE-0023", "Not authenticated user in the header"),
    USER_IS_NOT_PUBLISHER(HttpStatus.BAD_REQUEST, "SERVICE-0024", "User is not the publisher of the news"),
    INVALID_TOKEN_DIFFERENT_USER(HttpStatus.BAD_REQUEST, "SERVICE-0025", "Invalid token, different user"),
    INVALID_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "SERVICE-0026", "Invalid token, expired");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
