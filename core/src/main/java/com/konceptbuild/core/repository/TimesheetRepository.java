package com.konceptbuild.core.repository;

import com.konceptbuild.core.entity.TimesheetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TimesheetRepository extends JpaRepository<TimesheetEntity, UUID> {
    Optional<TimesheetEntity> findByWorkerIdAndYearAndMonth(
            UUID workerId,
            Integer year,
            Integer month);

    List<TimesheetEntity> findByYearAndMonth(Integer year, Integer month);
}
