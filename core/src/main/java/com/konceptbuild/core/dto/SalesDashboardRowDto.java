package com.konceptbuild.core.dto;

import com.konceptbuild.core.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesDashboardRowDto {
    ClientDto client;
    Double totalReceived;
    Double totalDue;
    Double totalOverdue;
    InvoiceStatus status;
}
