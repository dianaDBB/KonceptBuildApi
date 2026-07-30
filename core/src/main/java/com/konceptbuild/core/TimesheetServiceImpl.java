package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.entity.*;
import com.konceptbuild.core.enums.WorkerContractType;
import com.konceptbuild.core.repository.TimesheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TimesheetServiceImpl implements TimesheetService {
    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private HolidayService holidayService;

    @Override
    @Transactional(readOnly = true)
    public MonthlyTimesheetDto getMonthlyTimesheet(Integer year, Integer month) {
        List<WorkerDto> workers = cacheService.getAllActiveWorkers(year, month);
        List<TimesheetEntity> timesheets = timesheetRepository.findByYearAndMonth(year, month);
        Map<UUID, TimesheetEntity> timesheetByWorker = new LinkedHashMap<>();

        for (TimesheetEntity timesheet : timesheets) {
            timesheetByWorker.put(timesheet.getWorker().getId(), timesheet);
        }

        List<WorkerTimesheetDto> workerDtoList = new ArrayList<>();

        for (WorkerDto worker : workers) {
            TimesheetEntity timesheet = timesheetByWorker.get(worker.getId());

            WorkerTimesheetDto workerDto = WorkerTimesheetDto
                    .builder()
                    .worker(worker)
                    .expectedHours(0.0)
                    .totalHours(0.0)
                    .worksTimesheet(new ArrayList<>())
                    .build();

            if (timesheet != null) {
                workerDto.setTimesheetId(timesheet.getId());
                workerDto.setWorksTimesheet(buildLines(timesheet));

                workerDto.setExpectedHours(timesheet.getExpectedHours());
                workerDto.setTotalHours(timesheet.getTotalHours());
                workerDto.setTotalExtraHours(timesheet.getTotalExtraHours());
                workerDto.setTotalPaidAbsenceHours(timesheet.getTotalPaidAbsenceHours());
                workerDto.setTotalUnpaidAbsenceHours(timesheet.getTotalUnpaidAbsenceHours());
            }
            workerDtoList.add(workerDto);
        }

        workerDtoList.sort(Comparator.comparing(dto -> dto.getWorker().getName()));

        return MonthlyTimesheetDto.builder().year(year).month(month).workersTimesheet(workerDtoList).build();
    }

    private List<WorkTimesheetDto> buildLines(TimesheetEntity timesheet) {
        List<WorkTimesheetDto> works = new ArrayList<>();

        for (TimesheetLineEntity line : timesheet.getTimesheetLineEntities()) {
            List<DayEntryDto> days = line.getEntries()
                    .stream()
                    .sorted(Comparator.comparing(TimesheetEntryEntity::getDate))
                    .map(entry -> DayEntryDto.builder().date(entry.getDate()).hours(entry.getHours()).build())
                    .toList();

            works.add(WorkTimesheetDto.builder().work(line.getWork() == null ? null : new WorkDto(line.getWork())).attendanceCode(line.getAttendanceCode()).days(days).build());
        }

        return works;
    }

    @Override
    public void saveMonthlyTimesheet(MonthlyTimesheetDto dto) {
        for (WorkerTimesheetDto workerDto : dto.getWorkersTimesheet()) {
            WorkerDto worker =
                    cacheService.getWorker(workerDto.getWorker().getId()).orElseThrow(() -> new IllegalArgumentException("Worker not found: " + workerDto.getWorker().getId()));

            TimesheetEntity timesheet = timesheetRepository.findByWorkerIdAndYearAndMonth(worker.getId(),
                    dto.getYear(), dto.getMonth()).orElseGet(() -> {
                TimesheetEntity entity = new TimesheetEntity();
                entity.setWorker(new WorkerEntity(worker));
                entity.setYear(dto.getYear());
                entity.setMonth(dto.getMonth());
                entity.setTimesheetLineEntities(new ArrayList<>());
                return entity;
            });

            timesheet.getTimesheetLineEntities().clear();

            for (WorkTimesheetDto workDto : workerDto.getWorksTimesheet()) {
                TimesheetLineEntity line = new TimesheetLineEntity();
                line.setTimesheet(timesheet);

                if (workerDto.getWorker().getWorkerContractType() == WorkerContractType.CONTRACTOR && workDto.getAttendanceCode() != null) {
                    throw (new IllegalArgumentException("A contractor worker cannot have an absence. " + workerDto.getWorker().getName()));
                }

                if (workDto.getWork() != null) {
                    WorkDto work =
                            cacheService.getWork(workDto.getWork().getId()).orElseThrow(() -> new IllegalArgumentException("Work not found: " + workDto.getWork().getId()));

                    line.setWork(new WorkEntity(work));
                } else {
                    line.setAttendanceCode(workDto.getAttendanceCode());
                }

                line.setEntries(new ArrayList<>());

                for (DayEntryDto dayDto : workDto.getDays()) {
                    if (dayDto.getHours() == null) {
                        continue;
                    }

                    TimesheetEntryEntity entry = new TimesheetEntryEntity();
                    entry.setLine(line);
                    entry.setDate(dayDto.getDate());
                    entry.setHours(dayDto.getHours());

                    line.getEntries().add(entry);
                }
                timesheet.getTimesheetLineEntities().add(line);
            }

            validateInternalWorkerHours(timesheet, worker);
            updateExpectedHours(timesheet);
            updateTotals(timesheet);
            timesheetRepository.save(timesheet);
        }
    }

    private void validateInternalWorkerHours(TimesheetEntity timesheet, WorkerDto worker) {
        if (worker.getWorkerContractType() != WorkerContractType.INTERNAL) {
            return;
        }

        LocalDate firstDay = LocalDate.of(timesheet.getYear(), timesheet.getMonth(), 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        Map<LocalDate, Double> hoursPerDay = new HashMap<>();

        for (TimesheetLineEntity line : timesheet.getTimesheetLineEntities()) {
            for (TimesheetEntryEntity entry : line.getEntries()) {
                if (entry.getHours() == null) {
                    continue;
                }

                hoursPerDay.merge(entry.getDate(), entry.getHours(), Double::sum);
            }
        }

        for (LocalDate day = firstDay; !day.isAfter(lastDay); day = day.plusDays(1)) {
            switch (day.getDayOfWeek()) {
                case SATURDAY, SUNDAY -> {
                    continue;
                }
            }

            if (holidayService.isHoliday(day)) {
                continue;
            }

            double hours = hoursPerDay.getOrDefault(day, 0.0);

            if (hours < timesheet.getWorkerHistory().getDefaultHours()) {
                throw new IllegalArgumentException(String.format("Worker '%s' has %.1f hours on %s but should have at" +
                        " least %.1f.", worker.getName(), hours, day, timesheet.getWorkerHistory().getDefaultHours()));
            }
        }
    }

    private void updateExpectedHours(TimesheetEntity timesheet) {
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

        timesheet.setExpectedHours(timesheet.getWorkerHistory().getDefaultHours() * workingDays);
    }

    private void updateTotals(TimesheetEntity timesheet) {
        double totalNormalHours = 0.0;
        double totalExtraHours = 0.0;
        double totalPaidAbsenceHours = 0.0;
        double totalUnpaidAbsenceHours = 0.0;

        // Worked hours per day (to check extra hours based on day)
        Map<LocalDate, Double> workHoursPerDay = new HashMap<>();

        for (TimesheetLineEntity line : timesheet.getTimesheetLineEntities()) {

            // ABSENCE
            if (line.getAttendanceCode() != null) {
                for (TimesheetEntryEntity entry : line.getEntries()) {
                    if (entry.getHours() == null) {
                        continue;
                    }

                    if (line.getAttendanceCode().isPaid()) {
                        totalPaidAbsenceHours += entry.getHours();
                    } else {
                        totalUnpaidAbsenceHours += entry.getHours();
                    }
                }
                continue;
            }

            // WORK
            if (line.getWork() != null) {
                for (TimesheetEntryEntity entry : line.getEntries()) {
                    if (entry.getHours() == null) {
                        continue;
                    }

                    workHoursPerDay.merge(entry.getDate(), entry.getHours(), Double::sum);
                }
            }
        }

        // Split work hours into normal and extra (per day)
        for (double workedHours : workHoursPerDay.values()) {
            totalNormalHours += Math.min(workedHours, timesheet.getWorkerHistory().getDefaultHours());
            totalExtraHours += Math.max(0.0, workedHours - timesheet.getWorkerHistory().getDefaultHours());
        }

        timesheet.setTotalHours(totalNormalHours + totalExtraHours + totalPaidAbsenceHours + totalUnpaidAbsenceHours);
        timesheet.setTotalExtraHours(totalExtraHours);
        timesheet.setTotalPaidAbsenceHours(totalPaidAbsenceHours);
        timesheet.setTotalUnpaidAbsenceHours(totalUnpaidAbsenceHours);
    }
}