package com.fitsharingapp.domain.key.repository;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "public_key", schema = "app")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PublicKey {

    @Id
    private UUID id;

    @Column(name = "fs_user_id")
    private UUID fsUserId;

    @Column(name = "device_id")
    private UUID deviceId;

    private byte[] key;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

}
