package com.fitsharingapp.application.news.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NewsResponse(

        UUID id,
        UUID publisherFsUserId,
        String publisherUsername,
        String publisherProfilePicture,
        UUID receiverFsUserId,
        String data,
        LocalDateTime createdAt,
        boolean isLiked,
        int likes
) {

}
