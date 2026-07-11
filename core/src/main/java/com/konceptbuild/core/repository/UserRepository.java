package com.konceptbuild.core.repository;

import com.konceptbuild.core.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsernameAndEnabledTrue(String username);

    boolean existsByUsername(String username);
}
