package com.konceptbuild.core;

import com.konceptbuild.core.dto.WorkDto;
import com.konceptbuild.core.entity.WorkEntity;
import com.konceptbuild.core.enums.WorkStatus;
import com.konceptbuild.core.filter.*;
import com.konceptbuild.core.repository.WorkRepository;
import com.konceptbuild.core.request.WorkRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class WorkServiceImpl implements WorkService {
    @Autowired
    private CacheServiceImpl cacheServiceImpl;

    @Autowired
    private WorkRepository workRepository;

    @Override
    public List<WorkDto> search(WorkFilter filter) {
        Comparator<WorkDto> comparator = comparatorFor(filter.sortBy(), filter.sortDirection());

        // Keep inactive works at the end, except when sorting by status.
        if (filter.sortBy() != WorkSortField.STATUS) {
            comparator = Comparator
                    .comparing((WorkDto work) -> work.getStatus() == WorkStatus.DONE)
                    .thenComparing(comparator);
        }

        return cacheServiceImpl.getAllWorks().stream()
                .filter(work -> matchesString(work.getCode(), filter.code()))
                .filter(work -> matchesString(work.getName(), filter.name()))
                .filter(work -> filter.status() == null || filter.status() == work.getStatus())
                .filter(work -> isWithinRange(work.getContractedBudget(), filter.contractedBudgetMin(),
                        filter.contractedBudgetMax()))
                .filter(work -> isWithinRange(work.getEstimatedCost(), filter.estimatedCostMin(),
                        filter.estimatedCostMax()))
                .filter(work -> isWithinRange(work.getEstimatedCostMaterials(), filter.estimatedCostMaterialsMin(),
                        filter.estimatedCostMaterialsMax()))
                .filter(work -> isWithinRange(work.getEstimatedCostLabor(), filter.estimatedCostLaborMin(),
                        filter.estimatedCostLaborMax()))
                .filter(work -> isWithinRange(work.getEstimatedMarginEur(), filter.estimatedMarginEurMin(),
                        filter.estimatedMarginEurMax()))
                .filter(work -> isWithinRange(work.getEstimatedMarginPercentual(),
                        filter.estimatedMarginPercentualMin(), filter.estimatedMarginPercentualMax()))
                .filter(work -> isWithinRange(work.getStartDate(), filter.startDateMin(), filter.startDateMax()))
                .filter(work -> isWithinRange(work.getEstimatedEndDate(), filter.estimatedEndDateMin(),
                        filter.estimatedEndDateMax()))
                .filter(work -> isWithinRange(work.getEndDate(), filter.endDateMin(), filter.endDateMax()))
                .filter(work -> matchesString(work.getClient().getCompanyName(), filter.clientName()))
                .sorted(comparator)
                .toList();
    }

    private boolean matchesString(String value, String query) {
        return query == null || (value != null && value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));
    }

    private boolean isWithinRange(Double value, Double min, Double max) {
        return (min == null || value != null && value >= min) && (max == null || value != null && value <= max);
    }

    private boolean isWithinRange(LocalDate value, LocalDate min, LocalDate max) {
        return value == null || (min == null || !value.isBefore(min)) && (max == null || !value.isAfter(max));
    }

    private Comparator<WorkDto> comparatorFor(WorkSortField field, SortDirection sortDirection) {
        Comparator<String> stringComparator =
                sortDirection == SortDirection.DESC
                        ? Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed())
                        : Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);

        Comparator<Double> doubleComparator =
                sortDirection == SortDirection.DESC
                        ? Comparator.nullsLast(Comparator.reverseOrder())
                        : Comparator.nullsLast(Comparator.naturalOrder());

        Comparator<LocalDate> dateComparator =
                sortDirection == SortDirection.DESC
                        ? Comparator.nullsLast(Comparator.reverseOrder())
                        : Comparator.nullsLast(Comparator.naturalOrder());

        return switch (field) {
            case CODE -> Comparator.comparing(WorkDto::getCode, stringComparator);
            case NAME -> Comparator.comparing(WorkDto::getName, stringComparator);
            case STATUS -> Comparator.comparing(
                    WorkDto::getStatus,
                    sortDirection == SortDirection.DESC
                            ? Comparator.nullsLast(Comparator.reverseOrder())
                            : Comparator.nullsLast(Comparator.naturalOrder()));
            case CONTRACTED_BUDGET -> Comparator.comparing(WorkDto::getContractedBudget, doubleComparator);
            case ESTIMATED_COST -> Comparator.comparing(WorkDto::getEstimatedCost, doubleComparator);
            case ESTIMATED_COST_MATERIALS -> Comparator.comparing(WorkDto::getEstimatedCostMaterials, doubleComparator);
            case ESTIMATED_COST_LABOR -> Comparator.comparing(WorkDto::getEstimatedCostLabor, doubleComparator);
            case ESTIMATED_MARGIN_EUR -> Comparator.comparing(WorkDto::getEstimatedMarginEur, doubleComparator);
            case ESTIMATED_MARGIN_PERCENTUAL ->
                    Comparator.comparing(WorkDto::getEstimatedMarginPercentual, doubleComparator);
            case START_DATE -> Comparator.comparing(WorkDto::getStartDate, dateComparator);
            case ESTIMATED_END_DATE -> Comparator.comparing(WorkDto::getEstimatedEndDate, dateComparator);
            case END_DATE -> Comparator.comparing(WorkDto::getEndDate, dateComparator);
            case CLIENT_NAME -> Comparator.comparing(work -> work.getClient().getCompanyName(), stringComparator);
        };
    }

    @Override
    public void add(WorkRequest request) {
        workRepository.findByName(request.name())
                .ifPresent(work -> {
                    throw new IllegalArgumentException("Work already defined - " + request.name());
                });

        WorkEntity entity = new WorkEntity(request);
        workRepository.save(entity);
        cacheServiceImpl.refreshCache();
    }

    @Override
    public void update(WorkRequest request) {
        WorkEntity currentEntity = workRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Work already defined - " + request.name()));

        WorkEntity entity = new WorkEntity(request);
        entity.setCodeNumber(currentEntity.getCodeNumber());
        entity.setCode(currentEntity.getCode());
        workRepository.save(entity);
        cacheServiceImpl.refreshCache();
    }

    @Override
    public void delete(UUID id) {
        WorkEntity entity = workRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Work with ID " + id + " not found"));

        workRepository.delete(entity);
        cacheServiceImpl.refreshCache();
    }
}
