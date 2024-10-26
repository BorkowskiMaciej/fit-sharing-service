package com.fitsharingapp.application.news;

import com.fitsharingapp.application.news.dto.CreateNewsRequest;
import com.fitsharingapp.application.news.dto.CreateReferenceNewsRequest;
import com.fitsharingapp.application.news.dto.NewsResponse;
import com.fitsharingapp.domain.news.News;
import com.fitsharingapp.domain.news.ReferenceNews;
import com.fitsharingapp.domain.user.User;
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
    ReferenceNews toReferenceNewsEntity(CreateReferenceNewsRequest newsDTO, UUID fsUserId, UUID deviceId);

    @Mapping(target = "publisherUsername", source = "publisher.username")
    @Mapping(target = "publisherProfilePicture", expression = "java(" +
            "com.fitsharingapp.application.common.Base64Utils.bytesToBase64(publisher.getProfilePicture()))")
    @Mapping(target = "createdAt", source = "news.createdAt")
    NewsResponse toResponse(News news, User publisher);

    @Mapping(target = "publisherUsername", source = "publisher.username")
    @Mapping(target = "publisherProfilePicture", expression = "java(" +
            "com.fitsharingapp.application.common.Base64Utils.bytesToBase64(publisher.getProfilePicture()))")
    @Mapping(target = "createdAt", source = "news.createdAt")
    NewsResponse toResponse(ReferenceNews news, User publisher);

}
