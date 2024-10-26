package com.fitsharingapp.application.news.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateNewsRequest(

        @NotNull(message = "Reference news ID must not be null")
        UUID referenceNewsId,

        @NotNull(message = "Receiver FS User ID must not be null")
        UUID receiverFsUserId,

        @NotNull(message = "Receiver Device ID must not be null")
        UUID receiverDeviceId,

        @NotNull(message = "Data must not be null")
        String data

) {

}
