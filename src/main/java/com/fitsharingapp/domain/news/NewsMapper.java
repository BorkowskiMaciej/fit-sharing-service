package com.fitsharingapp.domain.news;

import com.fitsharingapp.application.news.dto.CreateNewsRequest;
import com.fitsharingapp.application.news.dto.NewsResponse;
import com.fitsharingapp.domain.news.repository.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publisherFsUserId", source = "fsUserId")
    News toEntity(CreateNewsRequest newsDTO, UUID fsUserId);

    @Mapping(target = "publisherUsername", source = "publisherUsername")
    @Mapping(target = "receiverUsername", source = "receiverUsername")
    NewsResponse toResponse(News news, String publisherUsername, String receiverUsername);


}
