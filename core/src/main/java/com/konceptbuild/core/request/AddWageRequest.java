package com.konceptbuild.core.request;

import com.konceptbuild.core.validator.ValidWage;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@ValidWage
public record AddWageRequest(
        @NotNull(message = "Year is required")
        int year,

        @NotNull(message = "Month is required")
        int month,

        @NotNull(message = "Worker ID is required")
        UUID workerId,

        @NotNull(message = "Timesheet ID is required")
        UUID timesheetId
) {
}
