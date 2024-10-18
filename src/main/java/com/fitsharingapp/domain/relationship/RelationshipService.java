package com.fitsharingapp.domain.relationship;

import com.fitsharingapp.application.relationship.dto.FriendsResponse;
import com.fitsharingapp.application.relationship.dto.RelationshipResponse;
import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.key.PublicKeyService;
import com.fitsharingapp.domain.key.repository.PublicKey;
import com.fitsharingapp.domain.relationship.repository.Relationship;
import com.fitsharingapp.domain.relationship.repository.RelationshipRepository;
import com.fitsharingapp.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.fitsharingapp.common.ErrorCode.*;
import static com.fitsharingapp.domain.relationship.repository.RelationshipStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final RelationshipMapper relationshipMapper;
    private final UserService userService;
    private final PublicKeyService publicKeyService;

    public Relationship createRelationship(UUID senderFsUserId, UUID recipientFsUserId) {
        userService.validateUser(recipientFsUserId, RECIPIENT_NOT_FOUND);
        validateRelationshipRequest(senderFsUserId, recipientFsUserId);
        Relationship relationship = Relationship.builder()
                .id(UUID.randomUUID())
                .sender(senderFsUserId)
                .recipient(recipientFsUserId)
                .status(PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return relationshipRepository.save(relationship);
    }

    public Relationship acceptRelationship(UUID recipient, UUID relationshipId) {
        Relationship relationship = relationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new ServiceException(ErrorCode.RELATIONSHIP_NOT_FOUND));
        if (!relationship.getRecipient().equals(recipient)) {
            throw new ServiceException(ErrorCode.USER_IS_NOT_RECIPIENT);
        }
        if (!(relationship.getStatus() == PENDING)) {
            throw new ServiceException(ErrorCode.RELATIONSHIP_HAS_NOT_PENDING_STATUS);
        }
        relationship.setStatus(ACCEPTED);
        relationship.setUpdatedAt(LocalDateTime.now());
        return relationshipRepository.save(relationship);
    }

    public void rejectRelationship(UUID recipient, UUID relationshipId) {
        Relationship relationship = relationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new ServiceException(ErrorCode.RELATIONSHIP_NOT_FOUND));
        if (!relationship.getRecipient().equals(recipient)) {
            throw new ServiceException(ErrorCode.USER_IS_NOT_RECIPIENT);
        }
        if (!(relationship.getStatus() == PENDING)) {
            throw new ServiceException(ErrorCode.RELATIONSHIP_HAS_NOT_PENDING_STATUS);
        }
        relationship.setStatus(REJECTED);
        relationship.setUpdatedAt(LocalDateTime.now());
        relationshipRepository.save(relationship);
    }

    public Relationship deleteRelationship(UUID fsUserId, UUID relationshipId) {
        Relationship relationship = relationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new ServiceException(ErrorCode.RELATIONSHIP_NOT_FOUND));
        if (relationship.getStatus() == ACCEPTED) {
            relationshipRepository.deleteById(relationshipId);
        } else if (relationship.getStatus() == PENDING) {
            if (!relationship.getSender().equals(fsUserId)) {
                throw new ServiceException(ErrorCode.USER_IS_NOT_SENDER);
            }
            relationshipRepository.deleteById(relationshipId);
        } else {
            throw new ServiceException(ErrorCode.CANNOT_DELETE_RELATIONSHIP);
        }
        return relationship;
    }

    public void deleteAllRelationships(UUID fsUserId) {
        relationshipRepository.deleteAllBySenderOrRecipient(fsUserId, fsUserId);
    }

    public Relationship getActiveRelationship(UUID fsUserId, UUID friendFsUserId) {
        return relationshipRepository.findBySenderAndRecipientAndStatusNot(fsUserId, friendFsUserId, REJECTED)
                .or(() -> relationshipRepository.findBySenderAndRecipientAndStatusNot(friendFsUserId, fsUserId, REJECTED))
                .orElseThrow(() -> new ServiceException(ErrorCode.RELATIONSHIP_NOT_FOUND));

    }

    public List<RelationshipResponse> getAcceptedRelationships(UUID fsUserId) {
        return Stream.concat(
                relationshipRepository.findAllByRecipientAndStatus(fsUserId, ACCEPTED)
                        .stream()
                        .map(relationship -> relationshipMapper.toRelationshipResponse(
                                relationship,
                                userService.getUserById(relationship.getSender(), USER_NOT_FOUND))),
                relationshipRepository.findAllBySenderAndStatus(fsUserId, ACCEPTED)
                        .stream()
                        .map(relationship -> relationshipMapper.toRelationshipResponse(
                                relationship,
                                userService.getUserById(relationship.getRecipient(), USER_NOT_FOUND))))
                .toList();
    }

    public List<RelationshipResponse> getReceivedRelationshipRequests(UUID fsUserId) {
        return relationshipRepository.findAllByRecipientAndStatus(fsUserId, PENDING)
                .stream()
                .map(relationship -> relationshipMapper.toRelationshipResponse(
                        relationship,
                        userService.getUserById(relationship.getSender(), USER_NOT_FOUND)))
                .toList();
    }

    public List<RelationshipResponse> getSentRelationshipRequests(UUID fsUserId) {
        return relationshipRepository.findAllBySenderAndStatus(fsUserId, PENDING)
                .stream()
                .map(relationship -> relationshipMapper.toRelationshipResponse(
                        relationship,
                        userService.getUserById(relationship.getRecipient(), USER_NOT_FOUND)))
                .toList();
    }

    private void validateRelationshipRequest(UUID sender, UUID recipient) {
        if (sender.equals(recipient)) {
            throw new ServiceException(ErrorCode.SELF_RELATIONSHIP);
        }
        if (relationshipRepository.existsBySenderAndRecipientAndStatus(sender, recipient, ACCEPTED)) {
            throw new ServiceException(ErrorCode.RELATIONSHIP_ALREADY_EXISTS);
        }
        if (relationshipRepository.existsBySenderAndRecipientAndStatus(recipient, sender, PENDING)) {
            throw new ServiceException(ErrorCode.PENDING_RELATIONSHIP);
        }
        if (relationshipRepository.existsBySenderAndRecipientAndStatus(sender, recipient, PENDING)) {
            throw new ServiceException(ErrorCode.PENDING_RELATIONSHIP);
        }

    }

    public void validateRelationship(UUID publisherFsUserId, UUID receiverFsUserId) {
        if (!relationshipRepository.existsBySenderAndRecipient(publisherFsUserId, receiverFsUserId) &&
                !relationshipRepository.existsBySenderAndRecipient(receiverFsUserId, publisherFsUserId)) {
            throw new ServiceException(ErrorCode.RELATIONSHIP_NOT_FOUND);
        }
    }

    public List<FriendsResponse> getFriends(UUID fsUserId) {
        return Stream.concat(
                relationshipRepository.findAllByRecipientAndStatus(fsUserId, ACCEPTED)
                        .stream()
                        .map(Relationship::getSender),
                relationshipRepository.findAllBySenderAndStatus(fsUserId, ACCEPTED)
                        .stream()
                        .map(Relationship::getRecipient))
                .map(uuid -> FriendsResponse.builder()
                        .fsUserId(uuid)
                        .publicKey(publicKeyService.getPublicKey(uuid)
                                .map(PublicKey::getKey)
                                .orElseThrow(() -> new ServiceException(PUBLIC_KEY_NOT_FOUND)))
                        .build())
                .toList();
    }

}
