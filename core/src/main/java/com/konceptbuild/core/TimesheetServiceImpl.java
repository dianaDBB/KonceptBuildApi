package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.entity.*;
import com.konceptbuild.core.enums.WorkerContractType;
import com.konceptbuild.core.repository.TimesheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TimesheetServiceImpl implements TimesheetService {
    @Autowired
    private TimesheetRepository timesheetRepository;

    @Autowired
    private CacheService cacheService;

    @Override
    @Transactional(readOnly = true)
    public MonthlyTimesheetDto getMonthlyTimesheet(Integer year, Integer month) {

        List<WorkerDto> workers = cacheService.getAllWorkers();
        List<TimesheetEntity> timesheets = timesheetRepository.findByYearAndMonth(year, month);

        Map<UUID, TimesheetEntity> timesheetByWorker = new LinkedHashMap<>();

        for (TimesheetEntity timesheet : timesheets) {
            timesheetByWorker.put(timesheet.getWorker().getId(), timesheet);
        }

        List<WorkerTimesheetDto> workerDtoList = new ArrayList<>();

        for (WorkerDto worker : workers) {

            TimesheetEntity timesheet = timesheetByWorker.get(worker.getId());

            WorkerTimesheetDto workerDto = WorkerTimesheetDto.builder()
                    .worker(worker)
                    .hourCost(worker.getHourRate())
                    .totalHours(0.0)
                    .totalCost(0.0)
                    .worksTimesheet(new ArrayList<>())
                    .build();

            if (timesheet != null) {

                workerDto.setWorksTimesheet(buildLines(timesheet));

                double totalHours = workerDto.getWorksTimesheet().stream()
                        .flatMap(work -> work.getDays().stream())
                        .mapToDouble(day -> day.getHours() == null ? 0 : day.getHours())
                        .sum();

                workerDto.setTotalHours(totalHours);

                switch (workerDto.getWorker().getWorkerContractType()) {
                    case WorkerContractType.INTERNAL -> workerDto.setTotalCost(workerDto.getWorker().getMonthlySalary());
                    case WorkerContractType.CONTRACTOR -> workerDto.setTotalCost(totalHours * workerDto.getHourCost());
                }
            }

            workerDtoList.add(workerDto);
        }

        workerDtoList.sort(Comparator.comparing(dto -> dto.getWorker().getName()));

        return MonthlyTimesheetDto.builder()
                .year(year)
                .month(month)
                .workersTimesheet(workerDtoList)
                .build();
    }

    private List<WorkTimesheetDto> buildLines(TimesheetEntity timesheet) {

        List<WorkTimesheetDto> works = new ArrayList<>();

        for (TimesheetLineEntity line : timesheet.getLines()) {

            List<DayEntryDto> days = line.getEntries()
                    .stream()
                    .sorted(Comparator.comparing(TimesheetEntryEntity::getDate))
                    .map(entry -> DayEntryDto.builder()
                            .date(entry.getDate())
                            .hours(entry.getHours())
                            .build())
                    .toList();

            works.add(
                    WorkTimesheetDto.builder()
                            .work(line.getWork() == null ? null : new WorkDto(line.getWork()))
                            .attendanceCode(line.getAttendanceCode())
                            .days(days)
                            .build()
            );
        }

        return works;
    }

    @Override
    public void saveMonthlyTimesheet(MonthlyTimesheetDto dto) {

        for (WorkerTimesheetDto workerDto : dto.getWorkersTimesheet()) {

            WorkerDto worker = cacheService.getWorker(workerDto.getWorker().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Worker not found: " + workerDto.getWorker().getId()));

            TimesheetEntity timesheet = timesheetRepository
                    .findByWorkerIdAndYearAndMonth(
                            worker.getId(),
                            dto.getYear(),
                            dto.getMonth())
                    .orElseGet(() -> {
                        TimesheetEntity entity = new TimesheetEntity();
                        entity.setWorker(new WorkerEntity(worker));
                        entity.setYear(dto.getYear());
                        entity.setMonth(dto.getMonth());
                        entity.setLines(new ArrayList<>());
                        return entity;
                    });

            timesheet.getLines().clear();

            for (WorkTimesheetDto workDto : workerDto.getWorksTimesheet()) {

                TimesheetLineEntity line = new TimesheetLineEntity();
                line.setTimesheet(timesheet);

                if (workDto.getWork() != null) {

                    WorkDto work = cacheService.getWork(workDto.getWork().getId())
                            .orElseThrow(() -> new IllegalArgumentException("Work not found: " + workDto.getWork().getId()));

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

                timesheet.getLines().add(line);
            }

            timesheetRepository.save(timesheet);
        }
    }

}