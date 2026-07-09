package com.konceptbuild.core;

import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.entity.WorkerEntity;
import com.konceptbuild.core.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerServiceImpl implements WorkerService {
    @Autowired
    private CacheServiceImpl cacheServiceImpl;

    @Autowired
    private WorkerRepository workerRepository;

    @Override
    public List<WorkerDto> getAll() {
        return cacheServiceImpl.getAllWorkers();
    }

    @Override
    public void add(WorkerDto request) {
        WorkerEntity entity = new WorkerEntity(request);

        workerRepository.save(entity);
        cacheServiceImpl.refreshCache();
    }
}
