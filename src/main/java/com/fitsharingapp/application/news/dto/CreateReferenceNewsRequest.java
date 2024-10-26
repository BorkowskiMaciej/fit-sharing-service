package com.fitsharingapp.application.news.dto;

import jakarta.validation.constraints.NotNull;

public record CreateReferenceNewsRequest(

        @NotNull(message = "Data must not be null")
        String data

) {

}
