package com.konceptbuild.core.request;

import com.konceptbuild.core.enums.PaymentMethod;
import com.konceptbuild.core.validator.ValidWage;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@ValidWage
public record UpdateWageRequest(
        @NotNull(message = "ID is required")
        UUID id,

        @NotNull(message = "Paid value is required")
        Double paidValue,

        @NotNull(message = "Paid date is required")
        LocalDate paidDate,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        String notes
) {
}
