package com.fitsharingapp.application.news.dto;

import java.util.UUID;

public record CreateNewsRequest(

        UUID referenceNewsId,
        UUID receiverFsUserId,
        String data
) {

}
