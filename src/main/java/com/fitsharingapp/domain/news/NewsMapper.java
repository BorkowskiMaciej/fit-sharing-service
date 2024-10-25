package com.fitsharingapp.domain.news;

import com.fitsharingapp.application.news.dto.CreateNewsRequest;
import com.fitsharingapp.application.news.dto.CreateReferenceNewsRequest;
import com.fitsharingapp.application.news.dto.NewsResponse;
import com.fitsharingapp.domain.news.repository.News;
import com.fitsharingapp.domain.news.repository.ReferenceNews;
import com.fitsharingapp.domain.user.repository.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Base64;
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
    @Mapping(target = "receiverUsername", source = "receiverUsername")
    @Mapping(target = "publisherProfilePicture", source = "publisher.profilePicture", qualifiedByName = "bytesToBase64")
    @Mapping(target = "createdAt", source = "news.createdAt")
    NewsResponse toResponse(News news, User publisher, String receiverUsername);

    @Mapping(target = "publisherUsername", source = "publisher.username")
    @Mapping(target = "publisherProfilePicture", source = "publisher.profilePicture", qualifiedByName = "bytesToBase64")
    @Mapping(target = "createdAt", source = "news.createdAt")
    NewsResponse toResponse(ReferenceNews news, User publisher);

    @Named("bytesToBase64")
    static String bytesToBase64(byte[] imageBytes) {
        if (imageBytes != null) {
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        }
        return null;
    }

}
