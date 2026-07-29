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
public class HrDashboardRowDto {
    private WorkDto workDto;
    private Double totalHours;
    private Double totalCost;
    private List<HrDashboardWorkerDto> workerDtoList;
}
