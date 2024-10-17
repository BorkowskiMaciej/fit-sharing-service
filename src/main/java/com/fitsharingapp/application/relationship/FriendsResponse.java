package com.fitsharingapp.application.relationship;

import lombok.Builder;

import java.util.UUID;

@Builder
public record FriendsResponse(

        UUID fsUserId,
        byte[] publicKey
) {

}
