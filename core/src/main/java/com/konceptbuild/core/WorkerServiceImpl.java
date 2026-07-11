package com.konceptbuild.core;

import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.dto.WorkerFilter;
import com.konceptbuild.core.dto.WorkerSortField;
import com.konceptbuild.core.dto.SortDirection;
import com.konceptbuild.core.entity.WorkerEntity;
import com.konceptbuild.core.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Comparator;
import java.util.Locale;

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
    public List<WorkerDto> getAll(WorkerFilter filter) {
        Comparator<WorkerDto> comparator = comparatorFor(filter.sortBy());
        if (filter.sortDirection() == SortDirection.DESC) {
            comparator = comparator.reversed();
        }

        return cacheServiceImpl.getAllWorkers().stream()
                .filter(worker -> filter.id() == null || filter.id().equals(worker.getId()))
                .filter(worker -> matchesName(worker.getName(), filter.name()))
                .filter(worker -> filter.workerType() == null || filter.workerType() == worker.getWorkerType())
                .filter(worker -> isWithinRange(worker.getHourRate(), filter.minHourRate(), filter.maxHourRate()))
                .filter(worker -> isWithinRange(worker.getMonthlySalary(), filter.minMonthlySalary(), filter.maxMonthlySalary()))
                .filter(worker -> isWithinRange(worker.getHourCost(), filter.minHourCost(), filter.maxHourCost()))
                .sorted(comparator)
                .toList();
    }

    private boolean matchesName(String value, String query) {
        return query == null || (value != null && value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));
    }

    private boolean isWithinRange(Double value, Double min, Double max) {
        return (min == null || value != null && value >= min) && (max == null || value != null && value <= max);
    }

    private Comparator<WorkerDto> comparatorFor(WorkerSortField field) {
        return switch (field) {
            case ID -> Comparator.comparing(WorkerDto::getId, Comparator.nullsLast(Comparator.naturalOrder()));
            case NAME -> Comparator.comparing(WorkerDto::getName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case WORKER_TYPE -> Comparator.comparing(WorkerDto::getWorkerType, Comparator.nullsLast(Comparator.naturalOrder()));
            case HOUR_RATE -> Comparator.comparing(WorkerDto::getHourRate, Comparator.nullsLast(Comparator.naturalOrder()));
            case MONTHLY_SALARY -> Comparator.comparing(WorkerDto::getMonthlySalary, Comparator.nullsLast(Comparator.naturalOrder()));
            case HOUR_COST -> Comparator.comparing(WorkerDto::getHourCost, Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }

    @Override
    public void add(WorkerDto request) {
        WorkerEntity entity = new WorkerEntity(request);

        workerRepository.save(entity);
        cacheServiceImpl.refreshCache();
    }
}
