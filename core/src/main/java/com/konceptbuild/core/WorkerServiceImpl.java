package com.konceptbuild.core;

import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.dto.WorkerFilter;
import com.konceptbuild.core.dto.WorkerSortField;
import com.konceptbuild.core.dto.SortDirection;
import com.konceptbuild.core.entity.WorkerEntity;
import com.konceptbuild.core.repository.WorkerRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public List<WorkerDto> search(WorkerFilter filter) {
        Comparator<WorkerDto> comparator = comparatorFor(filter.sortBy(), filter.sortDirection());

        return cacheServiceImpl.getAllWorkers().stream()
                .filter(worker -> matchesString(worker.getCode(), filter.code()))
                .filter(worker -> matchesString(worker.getName(), filter.name()))
                .filter(worker -> filter.status() == null || filter.status() == worker.getStatus())
                .filter(worker -> matchesString(worker.getContact(), filter.contact()))
                .filter(worker -> matchesString(worker.getEmail(), filter.email()))
                .filter(worker -> matchesString(worker.getFunction(), filter.function()))
                .filter(worker -> isWithinRange(worker.getDefaultHours(), filter.defaultHoursMin(),
                        filter.defaultHoursMax()))
                .filter(worker -> filter.contractType() == null || filter.contractType() == worker.getContractType())
                .filter(worker -> isWithinRange(worker.getHourRate(), filter.hourRateMin(), filter.hourRateMax()))
                .filter(worker -> isWithinRange(worker.getMonthlySalary(), filter.monthlySalaryMin(),
                        filter.monthlySalaryMax()))
                .filter(worker -> isWithinRange(worker.getTsu(), filter.tsuMin(), filter.tsuMax()))
                .filter(worker -> isWithinRange(worker.getMealAllowance(), filter.mealAllowanceMin(),
                        filter.mealAllowanceMax()))
                .filter(worker -> isWithinRange(worker.getAccidentInsurance(), filter.accidentInsuranceMin(),
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
            case STATUS -> Comparator.comparing(
                    WorkerDto::getStatus,
                    sortDirection == SortDirection.DESC
                            ? Comparator.nullsLast(Comparator.reverseOrder())
                            : Comparator.nullsLast(Comparator.naturalOrder()));
            case CONTACT -> Comparator.comparing(WorkerDto::getContact, stringComparator);
            case EMAIL -> Comparator.comparing(WorkerDto::getEmail, stringComparator);
            case FUNCTION -> Comparator.comparing(WorkerDto::getFunction, stringComparator);
            case DEFAULT_HOURS -> Comparator.comparing(WorkerDto::getDefaultHours, doubleComparator);
            case CONTRACT_TYPE -> Comparator.comparing(
                    WorkerDto::getContractType,
                    sortDirection == SortDirection.DESC
                            ? Comparator.nullsLast(Comparator.reverseOrder())
                            : Comparator.nullsLast(Comparator.naturalOrder()));
            case HOUR_RATE -> Comparator.comparing(WorkerDto::getHourRate, doubleComparator);
            case MONTHLY_SALARY -> Comparator.comparing(WorkerDto::getMonthlySalary, doubleComparator);
            case TSU -> Comparator.comparing(WorkerDto::getTsu, doubleComparator);
            case MEAL_ALLOWANCE -> Comparator.comparing(WorkerDto::getMealAllowance, doubleComparator);
            case ACCIDENT_INSURANCE -> Comparator.comparing(WorkerDto::getAccidentInsurance, doubleComparator);
            case START_DATE -> Comparator.comparing(WorkerDto::getStartDate, dateComparator);
            case END_DATE -> Comparator.comparing(WorkerDto::getEndDate, dateComparator);
        };
    }

    @Override
    public void add(WorkerDto request) {
        WorkerEntity entity = new WorkerEntity(request);

        workerRepository.save(entity);
        cacheServiceImpl.refreshCache();
    }
}
