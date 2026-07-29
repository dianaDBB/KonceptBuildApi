package com.konceptbuild.core;

import com.konceptbuild.core.dto.*;
import com.konceptbuild.core.entity.TimesheetEntity;
import com.konceptbuild.core.entity.TimesheetEntryEntity;
import com.konceptbuild.core.entity.TimesheetLineEntity;
import com.konceptbuild.core.repository.TimesheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class HumanResourcesServiceImpl implements HumanResourcesService {
    @Autowired
    private CacheService cacheService;

    @Autowired
    private TimesheetRepository timesheetRepository;

    @Override
    @Transactional(readOnly = true)
    public HrDashboardDto getDashboard(Integer year, Integer month) {

        List<TimesheetEntity> timesheets;

        if (year == null) {
            timesheets = timesheetRepository.findAll();
        } else if (month == null) {
            timesheets = timesheetRepository.findByYear(year);
        } else {
            timesheets = timesheetRepository.findByYearAndMonth(year, month);
        }

        Map<UUID, HrDashboardRowDto> workMap = new LinkedHashMap<>();

        for (TimesheetEntity timesheet : timesheets) {

            WorkerDto worker = cacheService.getWorker(timesheet.getWorker().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Worker not found: " + timesheet.getWorker().getId()));

            double hourCost = worker.getHourCost();

            for (TimesheetLineEntity line : timesheet.getTimesheetLineEntities()) {

                if (line.getWork() == null) {
                    continue;
                }

                double hours = line.getEntries().stream()
                        .map(TimesheetEntryEntity::getHours)
                        .filter(Objects::nonNull)
                        .mapToDouble(Double::doubleValue)
                        .sum();

                double cost = hours * hourCost;

                HrDashboardRowDto workRow = workMap.computeIfAbsent(
                        line.getWork().getId(),
                        id -> HrDashboardRowDto.builder()
                                .workDto(new WorkDto(line.getWork()))
                                .totalHours(0.0)
                                .totalCost(0.0)
                                .workerDtoList(new ArrayList<>())
                                .build()
                );

                workRow.setTotalHours(workRow.getTotalHours() + hours);
                workRow.setTotalCost(workRow.getTotalCost() + cost);

                HrDashboardWorkerDto workerRow = workRow.getWorkerDtoList()
                        .stream()
                        .filter(w -> w.getWorkerDto().getId().equals(worker.getId()))
                        .findFirst()
                        .orElseGet(() -> {
                            HrDashboardWorkerDto dto = HrDashboardWorkerDto.builder()
                                    .workerDto(worker)
                                    .totalHours(0.0)
                                    .totalCost(0.0)
                                    .build();

                            workRow.getWorkerDtoList().add(dto);
                            return dto;
                        });

                workerRow.setTotalHours(workerRow.getTotalHours() + hours);
                workerRow.setTotalCost(workerRow.getTotalCost() + cost);
            }
        }

        return HrDashboardDto.builder()
                .dashboard(new ArrayList<>(workMap.values()))
                .build();
    }
}
