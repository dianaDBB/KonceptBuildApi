package com.konceptbuild.core.repository;

import com.konceptbuild.core.entity.WorkerHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkerHistoryRepository extends JpaRepository<WorkerHistoryEntity, UUID> {
    Optional<WorkerHistoryEntity> findByWorkerIdAndValidToIsNull(UUID workerId);

    List<WorkerHistoryEntity> findByValidToIsNull();
}
