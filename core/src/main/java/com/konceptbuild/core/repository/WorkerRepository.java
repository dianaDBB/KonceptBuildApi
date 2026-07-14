package com.konceptbuild.core.repository;

import com.konceptbuild.core.entity.WorkerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkerRepository extends JpaRepository<WorkerEntity, UUID> {
    Optional<WorkerEntity> findByNif(String nif);
}
