package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface CacheService {
    void refreshCache();

    List<WorkerDto> getAllWorkers();

    List<WorkerDto> getAllActiveWorkers(Integer year, Integer month);

    Optional<WorkerDto> getWorker(UUID id);

    List<ClientDto> getAllClients();

    List<WorkDto> getAllWorks();

    Optional<WorkDto> getWork(UUID id);

    List<WageDto> getAllWages();

    Optional<WorkerHistoryDto> getWorkerHistory(UUID workerId, Integer year, Integer month);
}