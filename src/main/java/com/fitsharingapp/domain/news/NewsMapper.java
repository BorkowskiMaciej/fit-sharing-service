package com.fitsharingapp.domain.news;

import com.fitsharingapp.application.news.CreateNewsRequest;
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

}
