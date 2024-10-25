package com.fitsharingapp.application.news.dto;

import java.util.UUID;

public record CreateNewsRequest(

        UUID referenceNewsId,
        UUID receiverFsUserId,
        UUID receiverDeviceId,
        String data
) {

}
