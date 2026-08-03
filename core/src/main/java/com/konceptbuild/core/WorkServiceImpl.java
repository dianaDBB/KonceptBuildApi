package com.konceptbuild.core;

import com.konceptbuild.core.dto.ClientDto;
import com.konceptbuild.core.dto.WorkDto;
import com.konceptbuild.core.entity.ClientEntity;
import com.konceptbuild.core.entity.WorkEntity;
import com.konceptbuild.core.enums.WorkStatus;
import com.konceptbuild.core.filter.*;
import com.konceptbuild.core.repository.WorkRepository;
import com.konceptbuild.core.request.WorkRequest;
import com.konceptbuild.core.util.ComparatorBuilder;
import com.konceptbuild.core.util.FilterHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class WorkServiceImpl implements WorkService {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private WorkRepository workRepository;

    @Override
    public List<WorkDto> search(WorkFilter filter) {
        Comparator<WorkDto> comparator = ComparatorBuilder.buildComparator(
                filter.sortBy().fieldName(),
                filter.sortDirection(),
                WorkDto.class
        );

        // Keep inactive works at the end, except when sorting by status
        if (filter.sortBy() != WorkSortField.STATUS) {
            comparator = Comparator
                    .comparing((WorkDto work) -> work.getStatus() == WorkStatus.DONE)
                    .thenComparing(comparator);
        }

        return cacheService.getAllWorks().stream()
                .filter(work -> FilterHelper.matchesString(work.getCode(), filter.code()))
                .filter(work -> FilterHelper.matchesString(work.getName(), filter.name()))
                .filter(work -> FilterHelper.matchesEnum(work.getStatus(), filter.status()))
                .filter(work -> FilterHelper.isWithinRange(work.getContractedBudget(), filter.contractedBudget()))
                .filter(work -> FilterHelper.isWithinRange(work.getEstimatedCost(), filter.estimatedCost()))
                .filter(work -> FilterHelper.isWithinRange(work.getEstimatedCostMaterials(),
                        filter.estimatedCostMaterials()))
                .filter(work -> FilterHelper.isWithinRange(work.getEstimatedCostLabor(), filter.estimatedCostLabor()))
                .filter(work -> FilterHelper.isWithinRange(work.getEstimatedMarginEur(), filter.estimatedMarginEur()))
                .filter(work -> FilterHelper.isWithinRange(work.getEstimatedMarginPercentual(),
                        filter.estimatedMarginPercentual()))
                .filter(work -> FilterHelper.isWithinRange(work.getStartDate(), filter.startDate()))
                .filter(work -> FilterHelper.isWithinRange(work.getEstimatedEndDate(), filter.estimatedEndDate()))
                .filter(work -> FilterHelper.isWithinRange(work.getEndDate(), filter.endDate()))
                .filter(invoice -> FilterHelper.matchesString(
                        List.of(
                                invoice.getClient().getCode(),
                                invoice.getClient().getCompanyName(),
                                invoice.getClient().getNif(),
                                invoice.getClient().getContact(),
                                invoice.getClient().getEmail(),
                                invoice.getClient().getPhone()
                        ),
                        filter.client()
                ))
                .sorted(comparator)
                .toList();
    }

    @Override
    public void add(WorkRequest request) {
        workRepository.findByName(request.name())
                .ifPresent(work -> {
                    throw new IllegalArgumentException("Work already defined - " + request.name());
                });

        ClientDto clientDto = cacheService.getClientById(request.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found - " + request.clientId()));

        WorkEntity entity = new WorkEntity(request, new ClientEntity(clientDto));
        workRepository.save(entity);
        cacheService.refreshCache();
    }

    @Override
    public void update(WorkRequest request) {
        WorkEntity currentEntity = workRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Work already defined - " + request.name()));

        ClientDto clientDto = cacheService.getClientById(request.clientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found - " + request.clientId()));

        WorkEntity entity = new WorkEntity(request, new ClientEntity(clientDto));
        entity.setCodeNumber(currentEntity.getCodeNumber());
        entity.setCode(currentEntity.getCode());
        workRepository.save(entity);
        cacheService.refreshCache();
    }

    @Override
    public void delete(UUID id) {
        WorkEntity entity = workRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Work with ID " + id + " not found"));

        workRepository.delete(entity);
        cacheService.refreshCache();
    }
}
