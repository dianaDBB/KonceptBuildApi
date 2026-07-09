package com.konceptbuild.core;

import com.konceptbuild.core.dto.WorkerDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CacheService {
    void refreshCache();

    List<WorkerDto> getAllWorkers();
}