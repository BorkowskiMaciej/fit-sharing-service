package com.fitsharingapp.domain.news;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "news")
@Data
public class News {

    @Id
    private UUID id;
    private UUID referenceNewsId;

    private UUID publisherFsUserId;
    private UUID receiverFsUserId;
    private UUID receiverDeviceId;

    private Boolean isLiked;

    private String data;
    private LocalDateTime createdAt;


}
