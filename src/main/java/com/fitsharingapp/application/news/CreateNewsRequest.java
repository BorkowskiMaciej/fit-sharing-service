package com.fitsharingapp.application.news;

import java.util.UUID;

public record CreateNewsRequest(

        UUID receiverFsUserId,
        String activityType,
        String data
) {

}
