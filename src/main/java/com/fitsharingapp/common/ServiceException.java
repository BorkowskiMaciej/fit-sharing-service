package com.fitsharingapp.common;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final ErrorCode errorCode;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ServiceException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public static ServiceException withFormattedMessage(ErrorCode errorCode, Object... args) {
        return new ServiceException(errorCode, String.format(errorCode.getMessage(), args));
    }

}
