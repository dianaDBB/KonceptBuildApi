package com.konceptbuild.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "revoked_token")
@Getter
@NoArgsConstructor
public class RevokedTokenEntity {
    @Id
    private UUID id;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public RevokedTokenEntity(UUID id, Instant expiresAt) {
        this.id = id;
        this.expiresAt = expiresAt;
    }
}
