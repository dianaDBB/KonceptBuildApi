package com.konceptbuild.core.repository;

import com.konceptbuild.core.entity.RevokedTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RevokedTokenRepository extends JpaRepository<RevokedTokenEntity, UUID> {
}
