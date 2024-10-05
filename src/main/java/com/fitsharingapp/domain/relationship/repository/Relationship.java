package com.fitsharingapp.domain.relationship.repository;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "relationship", schema = "app")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Relationship {

    @Id
    private UUID id;
    private UUID sender;
    private UUID recipient;

    @Enumerated(EnumType.STRING)
    private RelationshipStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

