package com.konceptbuild.core.dto;

import com.konceptbuild.core.entity.TimesheetEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerTimesheetDto {
    private UUID timesheetId;
    private WorkerDto worker;

    private Double expectedHours;
    private Double totalHours;
    private Double totalExtraHours;
    private Double totalPaidAbsenceHours;
    private Double totalUnpaidAbsenceHours;

    private List<WorkTimesheetDto> worksTimesheet;

    public WorkerTimesheetDto(TimesheetEntity entity) {
        this.timesheetId = entity.getId();
        this.worker = new WorkerDto(entity.getWorker());
        this.expectedHours = entity.getExpectedHours();
        this.totalHours = entity.getTotalHours();
        this.totalExtraHours = entity.getTotalExtraHours();
        this.totalPaidAbsenceHours = entity.getTotalPaidAbsenceHours();
        this.totalUnpaidAbsenceHours = entity.getTotalUnpaidAbsenceHours();
        this.worksTimesheet = entity.getTimesheetLineEntities().stream().map(WorkTimesheetDto::new).toList();
    }
}
