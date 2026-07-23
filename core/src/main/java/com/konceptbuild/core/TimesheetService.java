package com.konceptbuild.core;

import com.konceptbuild.core.dto.MonthlyTimesheetDto;
import org.springframework.stereotype.Component;

@Component
public interface TimesheetService {
    MonthlyTimesheetDto getMonthlyTimesheet(Integer year, Integer month);

    void saveMonthlyTimesheet(MonthlyTimesheetDto dto);
}
