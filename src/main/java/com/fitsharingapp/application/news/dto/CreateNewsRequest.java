package com.fitsharingapp.application.news.dto;

import java.util.UUID;

public record CreateNewsRequest(

        UUID receiverFsUserId,
        String data
) {

}
