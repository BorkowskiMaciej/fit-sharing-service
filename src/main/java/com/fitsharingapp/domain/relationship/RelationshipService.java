package com.fitsharingapp.domain.relationship;

import com.fitsharingapp.application.RelationshipResponse;
import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.relationship.repository.Relationship;
import com.fitsharingapp.domain.relationship.repository.RelationshipRepository;
import com.fitsharingapp.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;
import static com.fitsharingapp.domain.relationship.repository.RelationshipStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final RelationshipMapper relationshipMapper;
    private final UserService userService;

    public Relationship createRelationship(UUID senderFsUserId, UUID recipientFsUserId) {
        validateRecipient(recipientFsUserId);
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

    public void deleteRelationship(UUID fsUserId, UUID relationshipId) {
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
    }

    public void deleteAllRelationships(UUID fsUserId) {
        relationshipRepository.deleteAllBySenderOrRecipient(fsUserId, fsUserId);
    }

    public List<RelationshipResponse> getAcceptedRelationships() {
        UUID fsUserId = (UUID) RequestContextHolder.currentRequestAttributes().getAttribute(FS_USER_ID_HEADER, RequestAttributes.SCOPE_REQUEST);
        return Stream.concat(
                relationshipRepository.findAllByRecipientAndStatus(fsUserId, ACCEPTED)
                        .stream()
                        .map(relationship -> relationshipMapper.toRelationshipResponse(relationship, userService.getUserById(relationship.getSender()).orElseThrow()))
                        .peek(relationshipResponse -> log.info("{}", relationshipResponse.toString())),
                relationshipRepository.findAllBySenderAndStatus(fsUserId, ACCEPTED)
                        .stream()
                        .map(relationship -> relationshipMapper.toRelationshipResponse(relationship, userService.getUserById(relationship.getRecipient()).orElseThrow()))
                        .peek(relationshipResponse -> log.info("{}", relationshipResponse.toString())))
                .toList();
    }

    public List<RelationshipResponse> getReceivedRelationshipRequests() {
        UUID fsUserId = (UUID) RequestContextHolder.currentRequestAttributes().getAttribute(FS_USER_ID_HEADER, RequestAttributes.SCOPE_REQUEST);
        return relationshipRepository.findAllByRecipientAndStatus(fsUserId, PENDING)
                .stream()
                .map(relationship -> relationshipMapper.toRelationshipResponse(relationship, userService.getUserById(relationship.getSender()).orElseThrow()))
                .toList();
    }

    public List<RelationshipResponse> getSentRelationshipRequests() {
        UUID fsUserId = (UUID) RequestContextHolder.currentRequestAttributes().getAttribute(FS_USER_ID_HEADER, RequestAttributes.SCOPE_REQUEST);
        return relationshipRepository.findAllBySenderAndStatus(fsUserId, PENDING)
                .stream()
                .map(relationship -> relationshipMapper.toRelationshipResponse(relationship, userService.getUserById(relationship.getRecipient()).orElseThrow()))
                .toList();
    }

    private void validateRecipient(UUID fsUserId) {
        userService.getUserById(fsUserId)
                .orElseThrow(() -> new ServiceException(ErrorCode.RECIPIENT_NOT_FOUND));
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
        if (!relationshipRepository.existsBySenderAndRecipient(publisherFsUserId, receiverFsUserId)) {
            throw new ServiceException(ErrorCode.RELATIONSHIP_NOT_FOUND);
        }
        if (!relationshipRepository.existsBySenderAndRecipientAndStatus(publisherFsUserId, receiverFsUserId, ACCEPTED)) {
            throw new ServiceException(ErrorCode.NOT_ACCEPTED_RELATIONSHIP);
        }
    }

    public Relationship getLastRelationship(UUID fsUserId, UUID friendFsUserId) {
        return relationshipRepository.findBySenderAndRecipientAndStatusNot(fsUserId, friendFsUserId, REJECTED)
                .or(() -> relationshipRepository.findBySenderAndRecipientAndStatusNot(friendFsUserId, fsUserId, REJECTED))
                .orElseThrow(() -> new ServiceException(ErrorCode.RELATIONSHIP_NOT_FOUND));

    }

}
