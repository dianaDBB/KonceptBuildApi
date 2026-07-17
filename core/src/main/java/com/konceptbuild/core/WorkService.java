package com.konceptbuild.core;

import com.konceptbuild.core.dto.WorkDto;
import com.konceptbuild.core.filter.WorkFilter;
import com.konceptbuild.core.request.WorkRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface WorkService {
    List<WorkDto> search(WorkFilter filter);

    void add(WorkRequest request);

    void update(WorkRequest request);

    void delete(UUID id);
}
