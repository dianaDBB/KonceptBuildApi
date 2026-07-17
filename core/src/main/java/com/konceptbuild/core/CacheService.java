package com.konceptbuild.core;

import com.konceptbuild.core.dto.ClientDto;
import com.konceptbuild.core.dto.WorkDto;
import com.konceptbuild.core.dto.WorkerDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CacheService {
    void refreshCache();

    List<WorkerDto> getAllWorkers();

    List<ClientDto> getAllClients();

    List<WorkDto> getAllWorks();
}