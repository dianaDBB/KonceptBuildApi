package com.konceptbuild.core.request;

import com.konceptbuild.core.enums.WorkStatus;
import com.konceptbuild.core.validator.ValidWork;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@ValidWork
public record WorkRequest(
        UUID id,

        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Status is required")
        WorkStatus status,

        @NotNull(message = "Contracted budget is required")
        Double contractedBudget,

        @NotNull(message = "Estimated cost materials is required")
        Double estimatedCostMaterials,

        @NotNull(message = "Estimated cost labor is required")
        Double estimatedCostLabor,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @NotNull(message = "Estimated end date is required")
        LocalDate estimatedEndDate,

        LocalDate endDate,

        @NotNull(message = "Client ID is required")
        UUID clientId
) {
}
