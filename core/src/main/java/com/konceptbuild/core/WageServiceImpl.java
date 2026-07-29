package com.konceptbuild.core;

import com.konceptbuild.core.dto.WageDto;
import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.entity.TimesheetEntity;
import com.konceptbuild.core.entity.WageEntity;
import com.konceptbuild.core.entity.WorkerEntity;
import com.konceptbuild.core.filter.SortDirection;
import com.konceptbuild.core.filter.WageFilter;
import com.konceptbuild.core.filter.WageSortField;
import com.konceptbuild.core.repository.TimesheetRepository;
import com.konceptbuild.core.repository.WageRepository;
import com.konceptbuild.core.request.AddWageRequest;
import com.konceptbuild.core.request.UpdateWageRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class WageServiceImpl implements WageService {
    @Autowired
    private CacheServiceImpl cacheServiceImpl;

    @Autowired
    private WageRepository wageRepository;

    @Autowired
    private TimesheetRepository timesheetRepository;

    @Override
    public List<WageDto> search(WageFilter filter) {
        Comparator<WageDto> comparator = comparatorFor(filter.sortBy(), filter.sortDirection());

        return cacheServiceImpl.getAllWages().stream()
                .filter(wage -> isWithinRange(wage.getYear(), filter.yearMin(), filter.yearMax()))
                .filter(wage -> isWithinRange(wage.getMonth(), filter.monthMin(), filter.monthMax()))
                .filter(wage -> matchesString(wage.getWorkerDto().getCode(), filter.workerCode()))
                .filter(wage -> matchesString(wage.getWorkerDto().getName(), filter.workerName()))
                .filter(wage -> isWithinRange(wage.getWorkerTimesheetDto().getTotalCost(), filter.expectedPayMin(),
                        filter.expectedPayMax()))
                .filter(wage -> isWithinRange(wage.getPaidValue(), filter.paidValueMin(), filter.paidValueMax()))
                .filter(wage -> isWithinRange(wage.getPaidDate(), filter.paidDateMin(), filter.paidDateMax()))
                .filter(wage -> filter.paymentMethod() == null || filter.paymentMethod() == wage.getPaymentMethod())
                .filter(wage -> matchesString(wage.getNotes(), filter.notes()))
                .sorted(comparator)
                .toList();
    }

    private boolean matchesString(String value, String query) {
        return query == null || (value != null && value.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)));
    }

    private boolean isWithinRange(Integer value, Integer min, Integer max) {
        return (min == null || value != null && value >= min) && (max == null || value != null && value <= max);
    }

    private boolean isWithinRange(Double value, Double min, Double max) {
        return (min == null || value != null && value >= min) && (max == null || value != null && value <= max);
    }

    private boolean isWithinRange(LocalDate value, LocalDate min, LocalDate max) {
        return value == null || (min == null || !value.isBefore(min)) && (max == null || !value.isAfter(max));
    }

    private Comparator<WageDto> comparatorFor(WageSortField field, SortDirection sortDirection) {
        Comparator<String> stringComparator =
                sortDirection == SortDirection.DESC
                        ? Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed())
                        : Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);

        Comparator<Integer> intComparator =
                sortDirection == SortDirection.DESC
                        ? Comparator.nullsLast(Comparator.reverseOrder())
                        : Comparator.nullsLast(Comparator.naturalOrder());

        Comparator<Double> doubleComparator =
                sortDirection == SortDirection.DESC
                        ? Comparator.nullsLast(Comparator.reverseOrder())
                        : Comparator.nullsLast(Comparator.naturalOrder());

        Comparator<LocalDate> dateComparator =
                sortDirection == SortDirection.DESC
                        ? Comparator.nullsLast(Comparator.reverseOrder())
                        : Comparator.nullsLast(Comparator.naturalOrder());

        return switch (field) {
            case CODE -> Comparator.comparing(WageDto::getCode, stringComparator);
            case YEAR -> Comparator.comparing(WageDto::getYear, intComparator);
            case MONTH -> Comparator.comparing(WageDto::getMonth, intComparator);
            case WORKER_CODE -> Comparator.comparing(wage -> wage.getWorkerDto().getCode(), stringComparator);
            case WORKER_NAME -> Comparator.comparing(wage -> wage.getWorkerDto().getName(), stringComparator);
            case EXPECTED_PAY ->
                    Comparator.comparing(wage -> wage.getWorkerTimesheetDto().getTotalCost(), doubleComparator);
            case PAID_VALUE -> Comparator.comparing(WageDto::getPaidValue, doubleComparator);
            case PAID_DATE -> Comparator.comparing(WageDto::getPaidDate, dateComparator);
            case PAYMENT_METHOD -> Comparator.comparing(
                    WageDto::getPaymentMethod,
                    sortDirection == SortDirection.DESC
                            ? Comparator.nullsLast(Comparator.reverseOrder())
                            : Comparator.nullsLast(Comparator.naturalOrder()));
            case NOTES -> Comparator.comparing(WageDto::getNotes, stringComparator);
        };
    }

    @Override
    public void add(AddWageRequest request) {
        WageEntity currentEntity = wageRepository.findByYearAndMonthAndWorkerId(request.year(), request.month(),
                request.workerId()).orElse(new WageEntity());

        WorkerDto workerDto =
                cacheServiceImpl.getWorker(request.workerId()).orElseThrow(() -> new EntityNotFoundException("Worker " +
                        "not found - " + request.workerId()));

        TimesheetEntity timesheetEntity =
                timesheetRepository.findById(request.timesheetId()).orElseThrow(() -> new EntityNotFoundException(
                        "Timesheet not found - " + request.workerId()));

        String code = "%d-%02d-%s".formatted(
                request.year(),
                request.month(),
                workerDto.getCode()
        );

        currentEntity.setCode(code);
        currentEntity.setYear(request.year());
        currentEntity.setMonth(request.month());
        currentEntity.setWorker(new WorkerEntity(workerDto));
        currentEntity.setTimesheet(timesheetEntity);

        wageRepository.save(currentEntity);
        cacheServiceImpl.refreshCache();
    }

    @Override
    public void update(UpdateWageRequest request) {
        WageEntity currentEntity = wageRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Wage not found - " + request.id()));

        currentEntity.setPaidValue(request.paidValue());
        currentEntity.setPaidDate(request.paidDate());
        currentEntity.setPaymentMethod(request.paymentMethod());
        currentEntity.setNotes(request.notes());
        wageRepository.save(currentEntity);
        cacheServiceImpl.refreshCache();
    }
}
