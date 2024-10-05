package com.fitsharingapp.domain.relationship.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, UUID> {

    boolean existsBySenderAndRecipientAndStatus(UUID sender, UUID recipient, RelationshipStatus status);
    boolean existsBySenderAndRecipient(UUID sender, UUID recipient);

    List<Relationship> findAllByRecipientAndStatus(UUID recipient, RelationshipStatus status);

    List<Relationship> findAllBySenderAndStatus(UUID sender, RelationshipStatus status);

    List<Relationship> findAllBySenderOrRecipientAndStatus(UUID sender, UUID recipient, RelationshipStatus status);

    void deleteAllBySenderOrRecipient(UUID fsUserId, UUID fsUserId1);

}
