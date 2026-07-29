package com.konceptbuild.core.repository;

import com.konceptbuild.core.entity.WageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WageRepository extends JpaRepository<WageEntity, UUID> {
    Optional<WageEntity> findByYearAndMonthAndWorkerId(Integer year, Integer month, UUID workerId);
}
