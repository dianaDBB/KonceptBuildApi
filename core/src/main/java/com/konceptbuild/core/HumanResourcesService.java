package com.konceptbuild.core;

import com.konceptbuild.core.dto.HrDashboardDto;
import org.springframework.stereotype.Component;

@Component
public interface HumanResourcesService {
    HrDashboardDto getDashboard(Integer year, Integer month);
}
