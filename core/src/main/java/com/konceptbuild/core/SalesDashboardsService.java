package com.konceptbuild.core;

import com.konceptbuild.core.dto.SalesDashboardDto;
import org.springframework.stereotype.Component;

@Component
public interface SalesDashboardsService {
    SalesDashboardDto getDashboard();
}
