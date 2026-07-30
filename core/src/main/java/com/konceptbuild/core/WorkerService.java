package com.konceptbuild.core;

import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.filter.WorkerFilter;
import com.konceptbuild.core.request.AddWorkerRequest;
import com.konceptbuild.core.request.UpdateWorkerRequest;
import com.konceptbuild.core.request.UpdateWorkerCompensationRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface WorkerService {
    List<WorkerDto> search(WorkerFilter filter);

    void add(AddWorkerRequest request);

    void update(UpdateWorkerRequest request);

    void updateCompensation(UpdateWorkerCompensationRequest request);

    void delete(UUID id);
}
