package com.konceptbuild.core;

import com.konceptbuild.core.dto.SalesClientReportDto;
import com.konceptbuild.core.dto.SalesDashboardDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface SalesDashboardsService {
    SalesDashboardDto getDashboard();

    SalesClientReportDto getClientReport(UUID clientId);
}
