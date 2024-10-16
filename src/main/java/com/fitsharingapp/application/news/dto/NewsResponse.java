package com.fitsharingapp.application.news.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NewsResponse(

        UUID id,
        UUID publisherFsUserId,
        String publisherUsername,
        UUID receiverFsUserId,
        String receiverUsername,
        String data,
        LocalDateTime createdAt
) {

}
