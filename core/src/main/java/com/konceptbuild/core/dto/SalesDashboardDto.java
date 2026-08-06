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
public class SalesDashboardDto {
    Double totalBilled;
    Double totalReceived;
    Double totalDue;
    List<SalesDashboardRowDto> clientsStatistics;
}
