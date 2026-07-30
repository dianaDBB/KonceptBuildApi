package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.entity.WorkerEntity;
import com.konceptbuild.core.entity.WorkerHistoryEntity;
import com.konceptbuild.core.enums.Status;
import com.konceptbuild.core.filter.SortDirection;
import com.konceptbuild.core.filter.WorkerFilter;
import com.konceptbuild.core.filter.WorkerSortField;
import com.konceptbuild.core.repository.WorkerHistoryRepository;
import com.konceptbuild.core.repository.WorkerRepository;
import com.konceptbuild.core.request.AddWorkerRequest;
import com.konceptbuild.core.request.UpdateWorkerRequest;
import com.konceptbuild.core.request.UpdateWorkerCompensationRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
import java.util.Locale;
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
        Comparator<WorkerDto> comparator = comparatorFor(filter.sortBy(), filter.sortDirection());

        // Keep inactive workers at the end, except when sorting by status.
        if (filter.sortBy() != WorkerSortField.STATUS) {
            comparator = Comparator
                    .comparing((WorkerDto worker) -> worker.getStatus() == Status.INACTIVE)
                    .thenComparing(comparator);
        }

        return cacheServiceImpl.getAllWorkers().stream()
                .filter(worker -> matchesString(worker.getCode(), filter.code()))
                .filter(worker -> matchesString(worker.getName(), filter.name()))
                .filter(worker -> matchesString(worker.getNif(), filter.nif()))
                .filter(worker -> filter.status() == null || filter.status() == worker.getStatus())
                .filter(worker -> matchesString(worker.getPhone(), filter.phone()))
                .filter(worker -> matchesString(worker.getEmail(), filter.email()))
                .filter(worker -> matchesString(worker.getFunction(), filter.function()))
                .filter(worker -> isWithinRange(worker.getCurrentWorkerCompensation().getHourCost(), filter.hourCostMin(), filter.hourCostMax()))
                .filter(worker -> isWithinRange(worker.getCurrentWorkerCompensation().getDefaultHours(), filter.defaultHoursMin(),
                        filter.defaultHoursMax()))
                .filter(worker -> filter.workerContractType() == null || filter.workerContractType() == worker.getWorkerContractType())
                .filter(worker -> isWithinRange(worker.getCurrentWorkerCompensation().getHourRate(), filter.hourRateMin(), filter.hourRateMax()))
                .filter(worker -> isWithinRange(worker.getCurrentWorkerCompensation().getMonthlySalary(), filter.monthlySalaryMin(),
                        filter.monthlySalaryMax()))
                .filter(worker -> isWithinRange(worker.getCurrentWorkerCompensation().getTsu(), filter.tsuMin(), filter.tsuMax()))
                .filter(worker -> isWithinRange(worker.getCurrentWorkerCompensation().getMealAllowance(), filter.mealAllowanceMin(),
                        filter.mealAllowanceMax()))
                .filter(worker -> isWithinRange(worker.getCurrentWorkerCompensation().getAccidentInsurance(), filter.accidentInsuranceMin(),
                        filter.accidentInsuranceMax()))
                .filter(worker -> isWithinRange(worker.getStartDate(), filter.startDateMin(), filter.startDateMax()))
                .filter(worker -> isWithinRange(worker.getEndDate(), filter.endDateMin(), filter.endDateMax()))
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
        return (min == null || (value != null && !value.isBefore(min))) && (max == null || (value != null && !value.isAfter(max)));
    }

    private Comparator<WorkerDto> comparatorFor(WorkerSortField field, SortDirection sortDirection) {
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
            case CODE -> Comparator.comparing(WorkerDto::getCode, stringComparator);
            case NAME -> Comparator.comparing(WorkerDto::getName, stringComparator);
            case NIF -> Comparator.comparing(WorkerDto::getNif, stringComparator);
            case STATUS -> Comparator.comparing(
                    WorkerDto::getStatus,
                    sortDirection == SortDirection.DESC
                            ? Comparator.nullsLast(Comparator.reverseOrder())
                            : Comparator.nullsLast(Comparator.naturalOrder()));
            case PHONE -> Comparator.comparing(WorkerDto::getPhone, stringComparator);
            case EMAIL -> Comparator.comparing(WorkerDto::getEmail, stringComparator);
            case FUNCTION -> Comparator.comparing(WorkerDto::getFunction, stringComparator);
            case HOUR_COST -> Comparator.comparing(worker -> worker.getCurrentWorkerCompensation().getHourCost(), doubleComparator);
            case DEFAULT_HOURS -> Comparator.comparing(worker -> worker.getCurrentWorkerCompensation().getDefaultHours(), doubleComparator);
            case CONTRACT_TYPE -> Comparator.comparing(
                    WorkerDto::getWorkerContractType,
                    sortDirection == SortDirection.DESC
                            ? Comparator.nullsLast(Comparator.reverseOrder())
                            : Comparator.nullsLast(Comparator.naturalOrder()));
            case HOUR_RATE -> Comparator.comparing(worker -> worker.getCurrentWorkerCompensation().getHourRate(), doubleComparator);
            case MONTHLY_SALARY -> Comparator.comparing(worker -> worker.getCurrentWorkerCompensation().getMonthlySalary(), doubleComparator);
            case TSU -> Comparator.comparing(worker -> worker.getCurrentWorkerCompensation().getTsu(), doubleComparator);
            case MEAL_ALLOWANCE -> Comparator.comparing(worker -> worker.getCurrentWorkerCompensation().getMealAllowance(), doubleComparator);
            case ACCIDENT_INSURANCE -> Comparator.comparing(worker -> worker.getCurrentWorkerCompensation().getAccidentInsurance(), doubleComparator);
            case START_DATE -> Comparator.comparing(WorkerDto::getStartDate, dateComparator);
            case END_DATE -> Comparator.comparing(WorkerDto::getEndDate, dateComparator);
        };
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
    public void delete(UUID id) {
        WorkerEntity entity = workerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Worker with ID " + id + " not found"));

        workerRepository.delete(entity);
        cacheServiceImpl.refreshCache();
    }
}
