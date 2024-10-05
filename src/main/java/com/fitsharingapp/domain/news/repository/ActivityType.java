package com.fitsharingapp.domain.news.repository;

import com.fitsharingapp.common.ServiceException;

import static com.fitsharingapp.common.ErrorCode.INVALID_ACTIVITY_TYPE;

public enum ActivityType {

    BIKE_RIDING,
    RUNNING,
    SWIMMING,
    WALKING;

    public static ActivityType validateAndGet(String activityType) {
        try {
            return ActivityType.valueOf(activityType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException(INVALID_ACTIVITY_TYPE);
        }
    }

}
