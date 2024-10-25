package com.fitsharingapp.domain.user;

import com.fitsharingapp.common.ServiceException;

import static com.fitsharingapp.common.ErrorCode.INVALID_DATA;

public enum UserGender {
    MALE,
    FEMALE,
    OTHER;

    public static UserGender validateAndGet(String gender) {
        try {
            return UserGender.valueOf(gender.toUpperCase());
        }
        catch (Exception e) {
            throw ServiceException.withFormattedMessage(INVALID_DATA, e.getMessage());
        }
    }

}