package com.fitsharingapp.domain.key.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublicKeyRepository extends JpaRepository<PublicKey, UUID> {

    void deleteAllByFsUserId(UUID fsUserId);

    List<PublicKey> findAllByFsUserId(UUID fsUserId);
    Optional<PublicKey> findByFsUserIdAndDeviceId(UUID fsUserId, UUID deviceId);

}
