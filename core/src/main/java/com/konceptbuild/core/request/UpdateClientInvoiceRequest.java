package com.konceptbuild.core.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateClientInvoiceRequest {
    @NotNull(message = "ID is required")
    UUID id;

    @NotNull(message = "Client ID is required")
    UUID clientId;

    @NotNull(message = "Work ID is required")
    UUID workId;

    @NotNull(message = "Description is required")
    String description;

    @NotNull(message = "Value without tax value is required")
    Double valueWithoutTax;

    @NotNull(message = "Applied tax value is required")
    Double appliedTax;

    @NotNull(message = "Tax value value is required")
    Double taxValue;

    @NotNull(message = "Total value value is required")
    Double totalValue;

    @NotNull(message = "Registration date is required")
    LocalDate registrationDate;

    @NotNull(message = "Due date is required")
    LocalDate dueDate;
}
