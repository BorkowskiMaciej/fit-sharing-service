package com.fitsharingapp.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // SERVICE-0xxx Authorization, headers, service errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVICE-0000", "Internal server error: %s."),
    MISSING_HEADER(HttpStatus.BAD_REQUEST, "SERVICE-0001", "Missing '%s' header."),
    INVALID_AUTHORIZATION_HEADER(HttpStatus.BAD_REQUEST, "SERVICE-0002", "Invalid authorization header."),
    INVALID_UUID_IN_HEADER(HttpStatus.BAD_REQUEST, "SERVICE-0003", "Invalid UUID in the %s header."),
    INVALID_UUID_IN_PATH(HttpStatus.BAD_REQUEST, "SERVICE-0004", "Invalid UUID in the path."),
    INVALID_DATA_FOR_FIELD(HttpStatus.BAD_REQUEST, "SERVICE-0005", "Invalid data for field '%s': '%s'."),
    INVALID_DATA(HttpStatus.BAD_REQUEST, "SERVICE-0006", "Invalid data: %s."),
    INVALID_CONTENT_TYPE(HttpStatus.BAD_REQUEST, "SERVICE-0007", "Invalid content type: %s."),
    NOT_AUTHENTICATED_USER_IN_HEADER(HttpStatus.NOT_FOUND, "SERVICE-0008", "Not authenticated user in the header."),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "SERVICE-0009", "Bad credentials."),
    INVALID_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "SERVICE-0010", "Expired token."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "SERVICE-0011", "Invalid token: %s."),
    INVALID_RESET_PASSWORD_CODE(HttpStatus.BAD_REQUEST, "SERVICE-0012", "Invalid reset password code."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-0013", "Resource '%s' not found."),

    // SERVICE-1xxx User, registration, login
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-1001", "User not found."),
    NOT_UNIQUE_USERNAME(HttpStatus.BAD_REQUEST, "SERVICE-1002", "Username already exists."),
    NOT_UNIQUE_EMAIL(HttpStatus.BAD_REQUEST, "SERVICE-1003", "Email already exists."),
    INVALID_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "SERVICE-1004", "Invalid old password."),

    // SERVICE-2xxx Relationship
    RELATIONSHIP_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SERVICE-2001", "Relationship already exists."),
    PENDING_RELATIONSHIP(HttpStatus.BAD_REQUEST, "SERVICE-2002", "Relationship is pending."),
    RECIPIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-2003", "Recipient not found."),
    SELF_RELATIONSHIP(HttpStatus.BAD_REQUEST, "SERVICE-2004", "Self relationship."),
    RELATIONSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-2005", "Relationship not found."),
    USER_IS_NOT_SENDER(HttpStatus.BAD_REQUEST, "SERVICE-2006", "User is not the sender of the relationship."),
    USER_IS_NOT_RECIPIENT(HttpStatus.BAD_REQUEST, "SERVICE-2007", "User is not the recipient of the relationship."),
    RELATIONSHIP_HAS_NOT_PENDING_STATUS(HttpStatus.BAD_REQUEST, "SERVICE-2008", "Relationship is not in pending status."),
    CANNOT_DELETE_RELATIONSHIP(HttpStatus.BAD_REQUEST, "SERVICE-2009", "Cannot delete relationship, relationship is not accepted or user is not the sender."),

    // SERVICE-3xxx News
    RECEIVER_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-3000", "Receiver not found."),
    PUBLISHER_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-3001", "Publisher not found."),
    USER_IS_NOT_PUBLISHER(HttpStatus.BAD_REQUEST, "SERVICE-3002", "User is not the publisher of the news."),

    // SERVICE-4xxx Keys
    PUBLIC_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-4000", "Public key not found."),
    PUBLIC_KEY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SERVICE-4001", "Public key already exists for this device."),
    DEVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "SERVICE-4002", "Device not found.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
