package com.konceptbuild.core;

import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.dto.WorkerFilter;
import com.konceptbuild.core.request.WorkerRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface WorkerService {
    List<WorkerDto> search(WorkerFilter filter);

    void add(WorkerRequest request);

    void update(WorkerRequest request);

    void delete(UUID id);
}
