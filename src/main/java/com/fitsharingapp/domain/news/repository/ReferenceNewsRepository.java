package com.fitsharingapp.domain.news.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReferenceNewsRepository extends MongoRepository<ReferenceNews, UUID> {

    List<ReferenceNews> findAllByPublisherFsUserIdAndDeviceId(UUID fsUserId, UUID deviceId, Sort sort);

    void deleteAllByPublisherFsUserId(UUID fsUserId);

}
