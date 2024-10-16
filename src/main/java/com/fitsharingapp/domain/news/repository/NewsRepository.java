package com.fitsharingapp.domain.news.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NewsRepository extends MongoRepository<News, UUID> {

    List<News> findAllByPublisherFsUserId(UUID fsUserId, Sort sort);

    List<News> findAllByReceiverFsUserId(UUID fsUserId, Sort sort);

    void deleteAllByPublisherFsUserId(UUID fsUserId);

    void deleteByPublisherFsUserIdAndReceiverFsUserId(UUID sender, UUID recipient);

    List<News> findAllByPublisherFsUserIdAndReceiverFsUserId(UUID friendFsUserId, UUID fsUserId);

}
