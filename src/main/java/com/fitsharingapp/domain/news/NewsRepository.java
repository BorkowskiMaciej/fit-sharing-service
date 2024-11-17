package com.fitsharingapp.domain.news;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NewsRepository extends MongoRepository<News, UUID> {

    List<News> findAllByReceiverFsUserIdAndReceiverDeviceId(UUID fsUserId, UUID deviceId, Sort sort);

    void deleteAllByPublisherFsUserId(UUID fsUserId);

    void deleteByPublisherFsUserIdAndReceiverFsUserId(UUID sender, UUID recipient);

    List<News> findAllByPublisherFsUserIdAndReceiverFsUserIdAndReceiverDeviceId(UUID friendFsUserId, UUID fsUserId, UUID deviceId, Sort sort);

    List<News> findAllByReferenceNewsId(UUID referenceNewsId);
}
