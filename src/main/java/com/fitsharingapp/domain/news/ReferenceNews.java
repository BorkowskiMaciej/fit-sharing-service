package com.fitsharingapp.domain.news;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "reference_news")
@Data
public class ReferenceNews {

    @Id
    private UUID id;
    private UUID publisherFsUserId;
    private UUID deviceId;

    private Integer likes;

    private String data;
    private LocalDateTime createdAt;

}
