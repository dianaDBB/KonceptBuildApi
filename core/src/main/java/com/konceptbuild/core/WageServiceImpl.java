package com.konceptbuild.core;

import com.konceptbuild.core.dto.WageDto;
import com.konceptbuild.core.dto.WorkerDto;
import com.konceptbuild.core.entity.TimesheetEntity;
import com.konceptbuild.core.entity.WageEntity;
import com.konceptbuild.core.entity.WorkerEntity;
import com.konceptbuild.core.filter.WageFilter;
import com.konceptbuild.core.repository.TimesheetRepository;
import com.konceptbuild.core.repository.WageRepository;
import com.konceptbuild.core.request.AddWageRequest;
import com.konceptbuild.core.request.UpdateWageRequest;
import com.konceptbuild.core.util.ComparatorBuilder;
import com.konceptbuild.core.util.FilterHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class WageServiceImpl implements WageService {
    @Autowired
    private CacheServiceImpl cacheServiceImpl;

    @Autowired
    private WageRepository wageRepository;

    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private HolidayService holidayService;

    @Override
    public List<WageDto> search(WageFilter filter) {
        Comparator<WageDto> comparator = ComparatorBuilder.buildComparator(
                filter.sortBy().fieldName(),
                filter.sortDirection(),
                WageDto.class
        );

        return cacheServiceImpl.getAllWages().stream()
                .filter(wage -> FilterHelper.matchesString(wage.getCode(), filter.code()))
                .filter(wage -> FilterHelper.isWithinRange(wage.getYear(), filter.year()))
                .filter(wage -> FilterHelper.isWithinRange(wage.getMonth(), filter.month()))
                .filter(wage -> FilterHelper.matchesString(wage.getWorkerTimesheetDto().getWorker().getName(),
                        filter.workerName()))
                .filter(wage -> FilterHelper.matchesString(wage.getWorkerTimesheetDto().getWorker().getCode(),
                        filter.workerCode()))
                .filter(wage -> FilterHelper.isWithinRange(wage.getExpectedWage(), filter.expectedWage()))
                .filter(wage -> FilterHelper.isWithinRange(wage.getExpectedExtraHours(), filter.expectedExtraHours()))
                .filter(wage -> FilterHelper.isWithinRange(wage.getExpectedDeductions(), filter.expectedDeductions()))
                .filter(wage -> FilterHelper.isWithinRange(wage.getExpectedInternalCost(),
                        filter.expectedInternalCost()))
                .filter(wage -> FilterHelper.isWithinRange(wage.getPaidValue(), filter.paidValue()))
                .filter(wage -> FilterHelper.isWithinRange(wage.getPaidDate(), filter.paidDate()))
                .filter(wage -> FilterHelper.matchesEnum(wage.getPaymentMethod(), filter.paymentMethod()))
                .filter(wage -> FilterHelper.matchesString(wage.getNotes(), filter.notes()))
                .sorted(comparator)
                .toList();
    }

    @Override
    public void add(AddWageRequest request) {
        WageEntity wageEntity = wageRepository.findByYearAndMonthAndWorkerId(request.year(), request.month(),
                request.workerId()).orElse(new WageEntity());

        WorkerDto workerDto =
                cacheServiceImpl.getWorkerById(request.workerId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Worker not found - " + request.workerId()
                        ));

        TimesheetEntity timesheetEntity =
                timesheetRepository.findById(request.timesheetId()).orElseThrow(() -> new EntityNotFoundException(
                        "Timesheet not found - " + request.workerId()));

        // Ignore if timesheet does not belong to the worker
        if (!timesheetEntity.getWorker().getId().equals(request.workerId()) || timesheetEntity.getTotalHours() <= 0) {
            return;
        }

        String code = "%d-%02d-%s".formatted(request.year(), request.month(), workerDto.getCode());

        wageEntity.setCode(code);
        wageEntity.setYear(request.year());
        wageEntity.setMonth(request.month());
        wageEntity.setWorker(new WorkerEntity(workerDto));
        wageEntity.setTimesheet(timesheetEntity);
        wageEntity.setWorkerHistory(timesheetEntity.getWorkerHistory());

        double hourlyRate = timesheetEntity.getWorkerHistory().getHourRate();
        double expectedExtraHoursCost = timesheetEntity.getTotalExtraHours() * hourlyRate;
        double expectedDeductionsCost = timesheetEntity.getTotalUnpaidAbsenceHours() * hourlyRate;

        double expectedWage = switch (workerDto.getWorkerContractType()) {
            case INTERNAL -> timesheetEntity.getWorkerHistory().getMonthlySalary()
                    + expectedExtraHoursCost
                    - expectedDeductionsCost
                    + Objects.requireNonNullElse(timesheetEntity.getWorkerHistory().getMealAllowance(), 0.0) * getWorkingDays(timesheetEntity);

            case CONTRACTOR ->
                    ((timesheetEntity.getTotalHours() - timesheetEntity.getTotalExtraHours()) * hourlyRate) + expectedExtraHoursCost;
        };

        wageEntity.setExpectedWage(expectedWage);
        wageEntity.setExpectedExtraHours(expectedExtraHoursCost);
        wageEntity.setExpectedDeductions(-expectedDeductionsCost);

        double expectedInternalCost = switch (workerDto.getWorkerContractType()) {
            case INTERNAL -> expectedWage
                    + timesheetEntity.getWorkerHistory().getAccidentInsurance()
                    + ((timesheetEntity.getWorkerHistory().getMonthlySalary() + expectedExtraHoursCost - expectedDeductionsCost) * timesheetEntity.getWorkerHistory().getTsu() / 100);

            case CONTRACTOR -> expectedWage;
        };

        wageEntity.setExpectedInternalCost(expectedInternalCost);

        wageRepository.save(wageEntity);
        cacheServiceImpl.refreshCache();
    }

    private int getWorkingDays(TimesheetEntity timesheet) {
        LocalDate firstDay = LocalDate.of(timesheet.getYear(), timesheet.getMonth(), 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        int workingDays = 0;

        for (LocalDate day = firstDay; !day.isAfter(lastDay); day = day.plusDays(1)) {
            switch (day.getDayOfWeek()) {
                case SATURDAY, SUNDAY -> {
                    continue;
                }
            }

            if (holidayService.isHoliday(day)) {
                continue;
            }

            workingDays++;
        }

        return workingDays;
    }

    @Override
    public void update(UpdateWageRequest request) {
        WageEntity currentEntity =
                wageRepository.findById(request.id()).orElseThrow(() -> new EntityNotFoundException("Wage not found -" +
                        " " + request.id()));

        currentEntity.setPaidValue(request.paidValue());
        currentEntity.setPaidDate(request.paidDate());
        currentEntity.setPaymentMethod(request.paymentMethod());
        currentEntity.setNotes(request.notes());
        wageRepository.save(currentEntity);
        cacheServiceImpl.refreshCache();
    }
}
