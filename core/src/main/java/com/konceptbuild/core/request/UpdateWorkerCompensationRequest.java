package com.konceptbuild.core.request;

import com.konceptbuild.core.validator.ValidWorker;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@ValidWorker
public record UpdateWorkerCompensationRequest(
        UUID workerId,

        @NotNull(message = "Valid from is required")
        LocalDate validFrom,

        Double defaultHours,

        Double hourRate,

        Double monthlySalary,

        Double tsu,

        Double mealAllowance,

        Double accidentInsurance
) {
}
