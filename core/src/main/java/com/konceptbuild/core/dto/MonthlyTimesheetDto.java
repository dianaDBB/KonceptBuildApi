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
public class MonthlyTimesheetDto {
    private int year;
    private int month;
    private List<WorkerTimesheetDto> workersTimesheet;
}
