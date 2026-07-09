package com.konceptbuild.core;

import com.konceptbuild.core.dto.WorkerDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface WorkerService {
    List<WorkerDto> getAll();

    void add(WorkerDto request);
}
