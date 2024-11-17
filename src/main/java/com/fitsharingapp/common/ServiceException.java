package com.fitsharingapp.common;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public ServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public static ServiceException withFormattedMessage(ErrorCode errorCode, Object... args) {
        return new ServiceException(errorCode, String.format(errorCode.getMessage(), args));
    }

}
