package com.konceptbuild.core;

import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.dto.WorkerFilter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface WorkerService {
    List<WorkerDto> search(WorkerFilter filter);

    void add(WorkerDto request);
}
