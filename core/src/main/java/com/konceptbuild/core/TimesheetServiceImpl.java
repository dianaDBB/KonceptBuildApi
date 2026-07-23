package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.entity.TimesheetEntity;
import com.konceptbuild.core.entity.TimesheetEntryEntity;
import com.konceptbuild.core.entity.WorkEntity;
import com.konceptbuild.core.entity.WorkerEntity;
import com.konceptbuild.core.repository.TimesheetRepository;
import com.konceptbuild.core.repository.WorkRepository;
import com.konceptbuild.core.repository.WorkerRepository;
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
    private WorkerRepository workerRepository;

    @Autowired
    private WorkRepository workRepository;

    @Override
    @Transactional(readOnly = true)
    public MonthlyTimesheetDto getMonthlyTimesheet(Integer year, Integer month) {

        List<WorkerEntity> workers = workerRepository.findAll();
        List<TimesheetEntity> timesheets = timesheetRepository.findByYearAndMonth(year, month);

        Map<UUID, TimesheetEntity> timesheetByWorker = new LinkedHashMap<>();

        for (TimesheetEntity timesheet : timesheets) {
            timesheetByWorker.put(timesheet.getWorker().getId(), timesheet);
        }

        List<WorkerTimesheetDto> workerDtoList = new ArrayList<>();

        for (WorkerEntity worker : workers) {

            TimesheetEntity timesheet = timesheetByWorker.get(worker.getId());

            WorkerTimesheetDto workerDto = WorkerTimesheetDto.builder()
                    .worker(new WorkerDto(worker))
                    .hourCost(worker.getHourRate())
                    .totalHours(0.0)
                    .totalCost(0.0)
                    .worksTimesheet(new ArrayList<>())
                    .build();

            if (timesheet != null) {

                workerDto.setWorksTimesheet(buildWorks(timesheet));

                double totalHours = workerDto.getWorksTimesheet().stream()
                        .flatMap(work -> work.getDays().stream())
                        .mapToDouble(day -> day.getHours() == null ? 0 : day.getHours())
                        .sum();

                workerDto.setTotalHours(totalHours);
                workerDto.setTotalCost(totalHours * workerDto.getHourCost());
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

    private List<WorkTimesheetDto> buildWorks(TimesheetEntity timesheet) {

        Map<WorkEntity, List<TimesheetEntryEntity>> entriesByWork = new LinkedHashMap<>();

        for (TimesheetEntryEntity entry : timesheet.getEntries()) {
            entriesByWork
                    .computeIfAbsent(entry.getWork(), work -> new ArrayList<>())
                    .add(entry);
        }

        List<WorkTimesheetDto> works = new ArrayList<>();

        for (Map.Entry<WorkEntity, List<TimesheetEntryEntity>> group : entriesByWork.entrySet()) {

            WorkEntity work = group.getKey();

            List<DayEntryDto> days = group.getValue()
                    .stream()
                    .sorted(Comparator.comparing(TimesheetEntryEntity::getDate))
                    .map(entry -> DayEntryDto.builder()
                            .date(entry.getDate())
                            .hours(entry.getHours() == null ? null : entry.getHours())
                            .attendanceCode(entry.getAttendanceCode())
                            .build())
                    .toList();

            works.add(
                    WorkTimesheetDto.builder()
                            .work(new WorkDto(work))
                            .days(days)
                            .build()
            );
        }

        works.sort(Comparator.comparing(dto -> dto.getWork().getCodeNumber()));

        return works;
    }

    @Override
    public void saveMonthlyTimesheet(MonthlyTimesheetDto dto) {

        for (WorkerTimesheetDto workerDto : dto.getWorkersTimesheet()) {

            WorkerEntity worker = workerRepository.findById(workerDto.getWorker().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Worker not found: " + workerDto.getWorker().getCode()));

            TimesheetEntity timesheet = timesheetRepository
                    .findByWorkerIdAndYearAndMonth(
                            worker.getId(),
                            dto.getYear(),
                            dto.getMonth())
                    .orElseGet(() -> {
                        TimesheetEntity entity = new TimesheetEntity();
                        entity.setWorker(worker);
                        entity.setYear(dto.getYear());
                        entity.setMonth(dto.getMonth());
                        entity.setEntries(new ArrayList<>());
                        return entity;
                    });

            timesheet.getEntries().clear();

            for (WorkTimesheetDto workDto : workerDto.getWorksTimesheet()) {

                WorkEntity work = workRepository.findById(workDto.getWork().getId())
                        .orElseThrow(() -> new IllegalArgumentException("Work not found: " + workDto.getWork().getCode()));

                for (DayEntryDto dayDto : workDto.getDays()) {
                    if (dayDto.getHours() == null && dayDto.getAttendanceCode() == null) {
                        continue;
                    }

                    TimesheetEntryEntity entry = new TimesheetEntryEntity();
                    entry.setTimesheet(timesheet);
                    entry.setWork(work);
                    entry.setDate(dayDto.getDate());
                    entry.setHours(dayDto.getHours() == null
                            ? null
                            : dayDto.getHours());
                    entry.setAttendanceCode(dayDto.getAttendanceCode());

                    timesheet.getEntries().add(entry);
                }
            }

            timesheetRepository.save(timesheet);
        }
    }

}