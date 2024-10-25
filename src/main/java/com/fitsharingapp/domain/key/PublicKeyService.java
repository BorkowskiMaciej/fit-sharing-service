package com.fitsharingapp.domain.key;

import com.fitsharingapp.application.key.dto.CreatePublicKeyRequest;
import com.fitsharingapp.domain.key.repository.PublicKey;
import com.fitsharingapp.domain.key.repository.PublicKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        PublicKey key = PublicKey.builder()
                .id(randomUUID())
                .fsUserId(fsUserId)
                .key(publicKey)
                .deviceId(deviceId)
                .generatedAt(now())
                .build();
        publicKeyRepository.save(key);
    }

    public List<PublicKey> getPublicKeys(UUID fsUserId) {
        return publicKeyRepository.findAllByFsUserId(fsUserId);
    }

    public void deleteKeys(UUID fsUserId) {
        publicKeyRepository.deleteAllByFsUserId(fsUserId);
    }

    public Optional<PublicKey> getPublicKey(UUID fsUserId, UUID deviceId) {
        return publicKeyRepository.findByFsUserIdAndDeviceId(fsUserId, deviceId);
    }

}
