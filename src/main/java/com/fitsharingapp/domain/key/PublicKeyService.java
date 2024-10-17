package com.fitsharingapp.domain.key;

import com.fitsharingapp.domain.key.repository.PublicKey;
import com.fitsharingapp.domain.key.repository.PublicKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class PublicKeyService {

    private final PublicKeyRepository publicKeyRepository;

    public void savePublicKey(UUID fsUserId, byte[] publicKey) {
        PublicKey key = PublicKey.builder()
                .id(randomUUID())
                .fsUserId(fsUserId)
                .key(publicKey)
                .generatedAt(now())
                .build();
        publicKeyRepository.save(key);
    }

    public Optional<PublicKey> getPublicKey(UUID fsUserId) {
        return publicKeyRepository.findByFsUserId(fsUserId);
    }

    public void deletePublicKey(UUID fsUserId) {
        publicKeyRepository.deleteAllByFsUserId(fsUserId);
    }

}
