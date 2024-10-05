package com.fitsharingapp.domain.news;

import com.fitsharingapp.domain.news.dto.CreateNewsDTO;
import com.fitsharingapp.domain.news.repository.ActivityType;
import com.fitsharingapp.domain.news.repository.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "publisherFsUserId", source = "fsUserId")
    @Mapping(target = "activityType", source = "activityType")
    News toEntity(CreateNewsDTO newsDTO, UUID fsUserId, ActivityType activityType);

}
