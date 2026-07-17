package com.konceptbuild.core.repository;

import com.konceptbuild.core.entity.WorkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkRepository extends JpaRepository<WorkEntity, UUID> {
    Optional<WorkEntity> findByName(String name);
}
