package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.entity.WorkerEntity;
import com.konceptbuild.core.entity.WorkerHistoryEntity;
import com.konceptbuild.core.enums.Status;
import com.konceptbuild.core.filter.WorkerFilter;
import com.konceptbuild.core.filter.WorkerSortField;
import com.konceptbuild.core.repository.WorkerHistoryRepository;
import com.konceptbuild.core.repository.WorkerRepository;
import com.konceptbuild.core.request.AddWorkerRequest;
import com.konceptbuild.core.request.UpdateWorkerRequest;
import com.konceptbuild.core.request.UpdateWorkerCompensationRequest;
import com.konceptbuild.core.util.ComparatorBuilder;
import com.konceptbuild.core.util.FilterHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
import java.util.UUID;

@Service
public class WorkerServiceImpl implements WorkerService {
    @Autowired
    private CacheServiceImpl cacheServiceImpl;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private WorkerHistoryRepository workerHistoryRepository;

    @Override
    public List<WorkerDto> search(WorkerFilter filter) {
        Comparator<WorkerDto> comparator = ComparatorBuilder.buildComparator(
                filter.sortBy().fieldName(),
                filter.sortDirection(),
                WorkerDto.class
        );

        // Keep inactive workers at the end, except when sorting by status.
        if (filter.sortBy() != WorkerSortField.STATUS) {
            comparator = Comparator
                    .comparing((WorkerDto worker) -> worker.getStatus() == Status.INACTIVE)
                    .thenComparing(comparator);
        }

        return cacheServiceImpl.getAllWorkers().stream()
                .filter(worker -> FilterHelper.matchesString(worker.getCode(), filter.code()))
                .filter(worker -> FilterHelper.matchesString(worker.getName(), filter.name()))
                .filter(worker -> FilterHelper.matchesString(worker.getNif(), filter.nif()))
                .filter(worker -> FilterHelper.matchesEnum(worker.getStatus(), filter.status()))
                .filter(worker -> FilterHelper.matchesString(worker.getPhone(), filter.phone()))
                .filter(worker -> FilterHelper.matchesString(worker.getEmail(), filter.email()))
                .filter(worker -> FilterHelper.matchesString(worker.getFunction(), filter.function()))
                .filter(worker -> FilterHelper.isWithinRange(worker.getCurrentWorkerCompensation().getHourCost(),
                        filter.hourCost()))
                .filter(worker -> FilterHelper.isWithinRange(worker.getCurrentWorkerCompensation().getDefaultHours(),
                        filter.defaultHours()))
                .filter(worker -> FilterHelper.matchesEnum(worker.getWorkerContractType(), filter.workerContractType()))
                .filter(worker -> FilterHelper.isWithinRange(worker.getCurrentWorkerCompensation().getHourRate(),
                        filter.hourRate()))
                .filter(worker -> FilterHelper.isWithinRange(worker.getCurrentWorkerCompensation().getMonthlySalary()
                        , filter.monthlySalary()))
                .filter(worker -> FilterHelper.isWithinRange(worker.getCurrentWorkerCompensation().getTsu(),
                        filter.tsu()))
                .filter(worker -> FilterHelper.isWithinRange(worker.getCurrentWorkerCompensation().getMealAllowance()
                        , filter.mealAllowance()))
                .filter(worker -> FilterHelper.isWithinRange(worker.getCurrentWorkerCompensation().getAccidentInsurance(), filter.accidentInsurance()))
                .filter(worker -> FilterHelper.isWithinRange(worker.getStartDate(), filter.startDate()))
                .filter(worker -> FilterHelper.isWithinRange(worker.getEndDate(), filter.endDate()))
                .sorted(comparator)
                .toList();
    }

    @Override
    public void add(AddWorkerRequest request) {
        workerRepository.findByNif(request.nif())
                .ifPresent(worker -> {
                    String userIdentifier = request.name() + " | " + request.nif();
                    throw new IllegalArgumentException("Worker already defined - " + userIdentifier);
                });

        WorkerEntity worker = workerRepository.save(new WorkerEntity(request));

        Double hourRate = switch (request.workerContractType()) {
            case CONTRACTOR -> request.hourRate();

            case INTERNAL -> {
                var monthlyCost = (request.monthlySalary() * 14 / 12);
                yield monthlyCost / 21 / request.defaultHours();
            }
        };

        Double hourCost = switch (request.workerContractType()) {
            case CONTRACTOR -> request.hourRate();

            case INTERNAL -> {
                var tsu = (request.monthlySalary() * (request.tsu() / 100)) * 14 / 12;
                var mealAllowance = (request.mealAllowance() * 21) * 11 / 12;
                var accidentInsurance = request.accidentInsurance();
                var monthlyCost = (request.monthlySalary() * 14 / 12) + tsu + mealAllowance + accidentInsurance;
                yield monthlyCost / 21 / request.defaultHours();
            }
        };

        WorkerHistoryEntity history = WorkerHistoryEntity.builder()
                .worker(worker)
                .hourCost(hourCost)
                .defaultHours(request.defaultHours())
                .hourRate(hourRate)
                .monthlySalary(request.monthlySalary())
                .tsu(request.tsu())
                .mealAllowance(request.mealAllowance())
                .accidentInsurance(request.accidentInsurance())
                .validFrom(request.startDate().withDayOfMonth(1))
                .build();

        workerHistoryRepository.save(history);
        cacheServiceImpl.refreshCache();
    }

    @Override
    public void update(UpdateWorkerRequest request) {
        String userIdentifier = request.name() + " | " + request.nif() + " | " + request.id();

        WorkerEntity currentEntity = workerRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Worker not found - " + userIdentifier));

        WorkerEntity entity = new WorkerEntity(request);
        entity.setCodeNumber(currentEntity.getCodeNumber());
        entity.setCode(currentEntity.getCode());

        workerRepository.save(entity);
        cacheServiceImpl.refreshCache();
    }

    @Override
    @Transactional
    public void updateCompensation(UpdateWorkerCompensationRequest request) {
        WorkerEntity worker = workerRepository.findById(request.workerId())
                .orElseThrow(() -> new EntityNotFoundException("Worker not found - " + request.workerId()));

        WorkerHistoryEntity oldHistory = workerHistoryRepository.findByWorkerIdAndValidToIsNull(worker.getId())
                .orElseThrow(() -> new EntityNotFoundException("Current worker history not found"));

        if (!request.validFrom().isAfter(oldHistory.getValidFrom())) {
            throw new IllegalArgumentException("The new 'validFrom' must be after the current 'validFrom'.");
        }

        LocalDate effectiveFrom = request.validFrom().withDayOfMonth(1);
        oldHistory.setValidTo(effectiveFrom.minusDays(1));


        Double hourRate = switch (worker.getWorkerContractType()) {
            case CONTRACTOR -> request.hourRate();

            case INTERNAL -> {
                var monthlyCost = (request.monthlySalary() * 14 / 12);
                yield monthlyCost / 21 / request.defaultHours();
            }
        };

        Double hourCost = switch (worker.getWorkerContractType()) {
            case CONTRACTOR -> request.hourRate();

            case INTERNAL -> {
                var tsu = (request.monthlySalary() * (request.tsu() / 100)) * 14 / 12;
                var mealAllowance = (request.mealAllowance() * 21) * 11 / 12;
                var accidentInsurance = request.accidentInsurance();
                var monthlyCost = (request.monthlySalary() * 14 / 12) + tsu + mealAllowance + accidentInsurance;
                yield monthlyCost / 21 / request.defaultHours();
            }
        };

        WorkerHistoryEntity newHistory = WorkerHistoryEntity.builder()
                .worker(worker)
                .hourCost(hourCost)
                .defaultHours(request.defaultHours())
                .hourRate(hourRate)
                .monthlySalary(request.monthlySalary())
                .tsu(request.tsu())
                .mealAllowance(request.mealAllowance())
                .accidentInsurance(request.accidentInsurance())
                .validFrom(effectiveFrom)
                .build();

        workerHistoryRepository.save(oldHistory);
        workerHistoryRepository.save(newHistory);

        workerHistoryRepository.flush();
        cacheServiceImpl.refreshCache();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        WorkerEntity entity = workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Worker with ID " + id + " not found"));

        workerHistoryRepository.deleteByWorker(entity);
        workerRepository.delete(entity);
        cacheServiceImpl.refreshCache();
    }
}
