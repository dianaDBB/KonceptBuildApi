package com.konceptbuild.core;

import de.focus_shift.jollyday.core.HolidayManager;
import de.focus_shift.jollyday.core.ManagerParameters;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class HolidayService {
    private final HolidayManager holidayManager = HolidayManager.getInstance(ManagerParameters.create("PT"));

    public boolean isHoliday(LocalDate date) {
        return holidayManager.isHoliday(date);
    }
}
