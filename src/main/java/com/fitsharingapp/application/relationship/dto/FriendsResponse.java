package com.fitsharingapp.application.relationship.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record FriendsResponse(

        UUID fsUserId,
        byte[] publicKey,
        UUID deviceId
) {

}
