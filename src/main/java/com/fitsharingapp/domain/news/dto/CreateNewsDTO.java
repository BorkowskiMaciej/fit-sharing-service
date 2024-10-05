package com.fitsharingapp.domain.news.dto;

import java.util.UUID;

public record CreateNewsDTO(

        UUID receiverFsUserId,
        String activityType,
        String data
) {

}
