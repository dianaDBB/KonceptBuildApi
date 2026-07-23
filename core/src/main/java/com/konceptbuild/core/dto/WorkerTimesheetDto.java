package com.konceptbuild.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerTimesheetDto {
    private WorkerDto worker;

    private Double hourCost;
    private Double totalHours;
    private Double totalCost;

    private List<WorkTimesheetDto> worksTimesheet;
}
