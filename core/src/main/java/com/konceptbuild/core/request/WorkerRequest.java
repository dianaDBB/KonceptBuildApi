package com.konceptbuild.core.request;

import com.konceptbuild.core.dto.WorkerContractType;
import com.konceptbuild.core.dto.Status;
import com.konceptbuild.core.validator.ValidWorker;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

@ValidWorker
public record WorkerRequest(
        UUID id,

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "NIF is required")
        String nif,

        @NotNull(message = "Status is required")
        Status status,

        @NotBlank(message = "Phone country code is required")
        @Pattern(
                regexp = "^\\+[1-9]\\d{0,2}$",
                message = "Invalid country code"
        )
        String phoneCountryCode,

        @NotBlank(message = "Phone is required")
        @Pattern(
                regexp = "^\\d{9}$",
                message = "Phone number must contain exactly 9 digits"
        )
        String phone,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        @Pattern(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Email must be a valid email address"
        )
        String email,

        @NotBlank(message = "Function is required")
        String function,

        @NotNull(message = "Contact is required")
        @DecimalMin("0.0")
        @DecimalMax("12.0")
        Double defaultHours,

        @NotNull(message = "ContactType is required")
        WorkerContractType workerContractType,

        Double hourRate,
        Double monthlySalary,
        Double tsu,
        Double mealAllowance,
        Double accidentInsurance,

        @NotNull(message = "StartDate is required")
        LocalDate startDate,

        LocalDate endDate
) {
}
