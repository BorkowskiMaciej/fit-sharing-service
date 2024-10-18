package com.fitsharingapp.domain.news;

import com.fitsharingapp.application.news.dto.CreateNewsRequest;
import com.fitsharingapp.application.news.dto.CreateReferenceNewsRequest;
import com.fitsharingapp.application.news.dto.NewsResponse;
import com.fitsharingapp.domain.news.repository.News;
import com.fitsharingapp.domain.news.repository.ReferenceNews;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publisherFsUserId", source = "fsUserId")
    News toNewsEntity(CreateNewsRequest newsDTO, UUID fsUserId);

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publisherFsUserId", source = "fsUserId")
    ReferenceNews toReferenceNewsEntity(CreateReferenceNewsRequest newsDTO, UUID fsUserId);

    @Mapping(target = "publisherUsername", source = "publisherUsername")
    @Mapping(target = "receiverUsername", source = "receiverUsername")
    NewsResponse toResponse(News news, String publisherUsername, String receiverUsername);

    NewsResponse toResponse(ReferenceNews news);


}
