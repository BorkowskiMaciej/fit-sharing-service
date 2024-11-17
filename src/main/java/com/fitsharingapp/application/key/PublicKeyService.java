package com.fitsharingapp.application.key;

import com.fitsharingapp.application.key.dto.CreatePublicKeyRequest;
import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.key.PublicKey;
import com.fitsharingapp.domain.key.PublicKeyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fitsharingapp.common.ErrorCode.PUBLIC_KEY_ALREADY_EXISTS;
import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class PublicKeyService {

    private final PublicKeyRepository publicKeyRepository;

    public void savePublicKey(UUID fsUserId, CreatePublicKeyRequest createPublicKeyRequest) {
        savePublicKey(fsUserId, createPublicKeyRequest.deviceId(), createPublicKeyRequest.publicKey());
    }

    public void savePublicKey(UUID fsUserId, UUID deviceId, byte[] publicKey) {
        getPublicKey(fsUserId, deviceId)
                .ifPresent(key -> {
                    throw new ServiceException(PUBLIC_KEY_ALREADY_EXISTS);
                });
        PublicKey key = PublicKey.builder()
                .id(randomUUID())
                .fsUserId(fsUserId)
                .key(publicKey)
                .deviceId(deviceId)
                .generatedAt(now())
                .build();
        publicKeyRepository.save(key);
    }

    public Optional<PublicKey> getPublicKey(UUID fsUserId, UUID deviceId) {
        return publicKeyRepository.findByFsUserIdAndDeviceId(fsUserId, deviceId);
    }

    @Transactional
    public void deleteKeys(UUID fsUserId) {
        publicKeyRepository.deleteAllByFsUserId(fsUserId);
    }

    public List<PublicKey> getPublicKeys(UUID fsUserId) {
        return publicKeyRepository.findAllByFsUserId(fsUserId);
    }

    public void validateDevice(UUID fsUserId, UUID deviceId, ErrorCode errorCode) {
        getPublicKey(fsUserId, deviceId)
                .orElseThrow(() -> new ServiceException(errorCode));
    }

}
