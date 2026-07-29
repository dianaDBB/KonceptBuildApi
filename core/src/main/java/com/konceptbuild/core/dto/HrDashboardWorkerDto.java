package com.konceptbuild.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HrDashboardWorkerDto {
    private WorkerDto workerDto;
    Double totalHours;
    Double totalCost;
}
